package com.blps.blps.service;

import com.blps.blps.dto.jms.OrderProcessingMessage;
import com.blps.blps.dto.request.OrderCreateRequest;
import com.blps.blps.dto.response.OrderResponse;
import com.blps.blps.entity.*;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.mapper.AddressMapper;
import com.blps.blps.mapper.OrderMapper;
import com.blps.blps.repository.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.yandex.tracker.jca.model.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final RestaurantRepository restaurantRepository;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderMapper orderMapper;
    private final AddressMapper addressMapper;
    private final TransactionTemplate transactionTemplate;
    private final YandexTrackerService trackerService;

    private final JmsTemplate jmsTemplate;
    private static final String QUEUE_PREPARATION = "order.preparation";
    private static final Integer DEFAULT_QUEUE_ID = 1;


    @Transactional
    public OrderResponse createOrderAsync(OrderCreateRequest request) {
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
        order.setTotalAmount(BigDecimal.ZERO);
        order.setItems(new ArrayList<>());

        String issueKey = null;
        String description = null;
        try {
            String summary = "Заказ #" + order.getId() + " от пользователя " + user.getName();
            description = String.format(
                    "Детали заказа:\nРесторан: %s\nКомментарий к ресторану: %s\nКомментарий к курьеру: %s\nОставить у двери: %s",
                    restaurant.getName(),
                    request.getCommentToRestaurant(),
                    request.getCommentToCourier(),
                    request.getLeaveAtDoor()
            );
            Issue createdIssue = trackerService.createIssue(summary, description, DEFAULT_QUEUE_ID);
            issueKey = createdIssue.getKey();
        } catch (Exception e) {
        }
        order.setYandexTrackerId(issueKey);
        Order savedOrder = orderRepository.save(order);

        try {
            trackerService.updateIssue(issueKey, "Заказ #" + order.getId() + " от пользователя " + user.getName(), description);
        } catch (Exception e) {}

        OrderProcessingMessage message = new OrderProcessingMessage(
                savedOrder.getId(),
                request.getUserId(),
                request.getRestaurantId(),
                request.getItems(),
                request.getAddress(),
                request.getCommentToRestaurant(),
                request.getCommentToCourier(),
                request.getLeaveAtDoor()
        );
        jmsTemplate.convertAndSend(QUEUE_PREPARATION, message);

        return orderMapper.toResponse(savedOrder);
    }

    public OrderResponse cancelOrderByCustomer(Long orderId, Long customerId) {
        return transactionTemplate.execute(status -> {
            try {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден: " + orderId));

                if (!order.getUser().getId().equals(customerId)) {
                    throw new BusinessException("Заказ не принадлежит данному клиенту");
                }

                OrderStatus currentStatus = order.getStatus();
                int refundPercent = getRefundPercent(currentStatus);

                if (refundPercent == 0) {
                    throw new BusinessException("Возврат невозможен для заказа в статусе " + currentStatus);
                }

                boolean refundSuccess = paymentService.processRefund(order, refundPercent);
                if (!refundSuccess) {
                    throw new BusinessException("Не удалось выполнить возврат");
                }

                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

                return orderMapper.toResponse(order);
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

    private int getRefundPercent(OrderStatus status) {
        return switch (status) {
            case PENDING,
                 CREATED,
                 WAITING_PAYMENT,
                 PAID,
                 CANCELLED_BY_REST,
                 PREPARING,
                 ASSIGNED -> 90;
            case READY -> 50;
            default -> 0;
        };
    }
}
