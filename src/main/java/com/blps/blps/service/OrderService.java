package com.blps.blps.service;

import com.blps.blps.dto.request.OrderCreateRequest;
import com.blps.blps.dto.request.OrderItemRequest;
import com.blps.blps.dto.response.OrderResponse;
import com.blps.blps.entity.*;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.mapper.AddressMapper;
import com.blps.blps.mapper.OrderMapper;
import com.blps.blps.repository.*;
import com.blps.blps.utils.DeliveryTimeCalculator;
import com.blps.blps.utils.DistanceCalculator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final RestaurantRepository restaurantRepository;
    private final ProductService productService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final DistanceCalculator distanceCalculator;
    private final DeliveryTimeCalculator deliveryTimeCalculator;
    private final TransactionTemplate transactionTemplate;

    public OrderResponse createOrder(OrderCreateRequest request) {
        return transactionTemplate.execute(status -> {
            try {
                if (request.getItems() == null || request.getItems().isEmpty()) {
                    throw new BusinessException("Заказ не может быть пустым. Добавьте хотя бы одно блюдо.");
                }

                User user = userService.getUserById(request.getUserId());
                Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ресторан не найден: " + request.getRestaurantId()));

                Address deliveryAddress;
                if (request.getAddress() != null && request.getAddress().getCity() != null) {
                    deliveryAddress = addressMapper.toEntity(request.getAddress());
                    deliveryAddress = addressService.save(deliveryAddress);
                } else {
                    deliveryAddress = user.getAddress();
                    if (deliveryAddress == null) {
                        throw new BusinessException("У пользователя не указан адрес, и не передан адрес в запросе");
                    }
                }

                Order order = new Order();
                order.setUser(user);
                order.setRestaurant(restaurant);
                order.setDeliveryAddress(deliveryAddress);
                order.setCommentToRestaurant(request.getCommentToRestaurant());
                order.setCommentToCourier(request.getCommentToCourier());
                order.setLeaveAtDoor(request.getLeaveAtDoor());
                order.setStatus(OrderStatus.CREATED);

                BigDecimal total = BigDecimal.ZERO;
                List<OrderItem> orderItems = new ArrayList<>();

                for (OrderItemRequest itemRequest : request.getItems()) {
                    Product product = productService.getProductById(itemRequest.getProductId());

                    if (!product.isAvailable()) {
                        throw new BusinessException("Товар '" + product.getName() + "' временно недоступен");
                    }

                    if (!product.getRestaurant().getId().equals(restaurant.getId())) {
                        throw new BusinessException("Товар '" + product.getName() + "' не принадлежит ресторану " + restaurant.getName());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setPrice(product.getPrice());
                    orderItems.add(orderItem);

                    total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
                }

                order.setTotalAmount(total);
                order.setItems(orderItems);
                Order savedOrder = orderRepository.save(order);

                Address restaurantAddress = restaurant.getAddress();
                boolean isWithinDistance = distanceCalculator.isWithinDeliveryDistance(restaurantAddress, deliveryAddress);

                if (!isWithinDistance) {
                    savedOrder.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(savedOrder);
                    return orderMapper.toResponse(savedOrder);
                }

                savedOrder.setStatus(OrderStatus.WAITING_PAYMENT);
                orderRepository.save(savedOrder);

                boolean paymentSuccess = paymentService.processPayment(savedOrder);
                if (!paymentSuccess) {
                    savedOrder.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(savedOrder);
                    throw new BusinessException("Оплата не прошла. Заказ отменён.");
                }

                savedOrder.setStatus(OrderStatus.PAID);
                orderRepository.save(savedOrder);

                Integer deliveryTime = deliveryTimeCalculator.calculateDeliveryTime(restaurantAddress, deliveryAddress);
                savedOrder.setEstimatedDeliveryTime(deliveryTime);
                orderRepository.save(savedOrder);

                return orderMapper.toResponse(savedOrder);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    public OrderResponse getOrderResponseByOrderId(Long id) {
        Order order =
                orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Заказ не найден: " + id));
        return orderMapper.toResponse(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Заказ не найден: " + id));
    }

    public Order getOrderByIdAndCourierId(Long id, Long courierId) {
        return orderRepository
                .findByIdAndCourierId(id, courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден или не принадлежит курьеру"));
    }

    public List<Order> getListOfOrdersByCourierIdAndStatus(Long courierId, OrderStatus status) {
        return orderRepository.findByCourierIdAndStatus(courierId, status);
    }

    public Order getOrderByIdAndRestaurantId(Long id, Long restaurantId) {
        return orderRepository
                .findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Заказ не найден или не принадлежит ресторану с id " + restaurantId));
    }

    public List<Order> getListOfOrdersByRestaurantIdAndStatus(Long restaurantId, OrderStatus status) {
        return orderRepository.findByRestaurantIdAndStatus(restaurantId, status);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
