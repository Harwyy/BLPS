package com.blps.blps.service;

import com.blps.blps.dto.OrderCheckResponse;
import com.blps.blps.dto.OrderInfoResponse;
import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.dto.OrderRequest;
import com.blps.blps.entity.*;
import com.blps.blps.entity.enums.OrderPaymentStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.mapper.AddressMapper;
import com.blps.blps.mapper.OrderInfoResponseMapper;
import com.blps.blps.repository.*;
import com.blps.blps.util.DeliveryCalculator;
import com.blps.blps.validation.OrderValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Data
    private static class ValidatedOrderData {
        private final boolean success;
        private final String message;
        private final User user;
        private final Restaurant restaurant;
        private final Address deliveryAddress;
        private final double distance;
        private final List<OrderItemDto> validatedItems;
        private final double total;
        private final int deliveryTime;

        public ValidatedOrderData(
                User user,
                Restaurant restaurant,
                Address deliveryAddress,
                double distance,
                List<OrderItemDto> validatedItems,
                double total,
                int deliveryTime) {
            this.success = true;
            this.message = null;
            this.user = user;
            this.restaurant = restaurant;
            this.deliveryAddress = deliveryAddress;
            this.distance = distance;
            this.validatedItems = validatedItems;
            this.total = total;
            this.deliveryTime = deliveryTime;
        }

        public ValidatedOrderData(String message) {
            this.success = false;
            this.message = message;
            this.user = null;
            this.restaurant = null;
            this.deliveryAddress = null;
            this.distance = 0;
            this.validatedItems = null;
            this.total = 0;
            this.deliveryTime = 0;
        }
    }

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final DeliveryCalculator deliveryCalculator;
    private final OrderValidator orderValidator;
    private final AddressMapper addressMapper;
    private final OrderInfoResponseMapper orderInfoResponseMapper;

    public OrderInfoResponse getOrderById(Long id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException("Заказ с id " + id + " не найден"));
        OrderInfoResponse response = orderInfoResponseMapper.mapToOrderInfoResponse(order);
        response.setSuccess(true);
        response.setMessage("Заказ найден");
        return response;
    }

    @Transactional(readOnly = true)
    public OrderCheckResponse checkOrder(OrderRequest request) {
        ValidatedOrderData validation = validateOrder(request, true);
        if (!validation.success) {
            OrderCheckResponse errorResponse = new OrderCheckResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(validation.message);
            return errorResponse;
        }

        OrderCheckResponse response = new OrderCheckResponse();
        response.setSuccess(true);
        response.setMessage("Заказ может быть оформлен");
        response.setTotalAmount(validation.total);
        response.setEstimatedDeliveryTime(validation.deliveryTime);
        response.setItems(validation.validatedItems);
        return response;
    }

    @Transactional
    public OrderInfoResponse confirmOrder(OrderRequest request) {
        ValidatedOrderData validation = validateOrder(request, false);
        if (!validation.success) {
            OrderInfoResponse errorResponse = new OrderInfoResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(validation.message);
            return errorResponse;
        }

        Order order = new Order();
        order.setUser(validation.user);
        order.setRestaurant(validation.restaurant);
        order.setDeliveryAddress(validation.deliveryAddress);
        order.setTotalAmount(BigDecimal.valueOf(validation.total));
        order.setPaymentStatus(OrderPaymentStatus.PENDING);
        order.setStatus(OrderStatus.SENT_TO_RESTAURANT);
        order.setEstimatedDeliveryTime(validation.deliveryTime);

        Order finalOrder = order;
        List<OrderItem> orderItems = validation.validatedItems.stream()
                .map(itemDto -> {
                    Product product = productRepository
                            .findById(itemDto.getProductId())
                            .orElseThrow(() -> new IllegalStateException("Товар не найден после валидации"));
                    OrderItem item = new OrderItem();
                    item.setOrder(finalOrder);
                    item.setProduct(product);
                    item.setQuantity(itemDto.getQuantity());
                    item.setPrice(BigDecimal.valueOf(itemDto.getPrice()));
                    return item;
                })
                .collect(Collectors.toList());
        order.setItems(orderItems);

        order = orderRepository.save(order);

        processPayment(order);

        OrderInfoResponse successResponse = orderInfoResponseMapper.mapToOrderInfoResponse(order);
        successResponse.setSuccess(true);
        successResponse.setMessage("Заказ успешно создан");
        return successResponse;
    }

    private void processPayment(Order order) {
        order.setPaymentStatus(OrderPaymentStatus.PAID);
        orderRepository.save(order);
    }

    private ValidatedOrderData validateOrder(OrderRequest request, boolean isCheckOnly) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return new ValidatedOrderData("Пользователь не найден");
        }

        Restaurant restaurant =
                restaurantRepository.findById(request.getRestaurantId()).orElse(null);
        if (restaurant == null) {
            return new ValidatedOrderData("Ресторан не найден");
        }

        Optional<String> restaurantError = orderValidator.validateRestaurantStatus(restaurant);
        if (restaurantError.isPresent()) {
            return new ValidatedOrderData(restaurantError.get());
        }

        Address deliveryAddress;
        if (request.getNewAddress() != null) {
            deliveryAddress = addressMapper.toEntity(request.getNewAddress());
            if (!isCheckOnly) {
                deliveryAddress = addressRepository.save(deliveryAddress);
            }
        } else {
            deliveryAddress = user.getAddress();
            if (deliveryAddress == null) {
                return new ValidatedOrderData("У пользователя не указан адрес доставки");
            }
        }

        Address restaurantAddress = restaurant.getAddress();
        if (restaurantAddress == null) {
            return new ValidatedOrderData("У ресторана не указан адрес");
        }

        double distance = deliveryCalculator.calculateDistance(
                restaurantAddress.getLatitude(), restaurantAddress.getLongitude(),
                deliveryAddress.getLatitude(), deliveryAddress.getLongitude());

        Optional<String> distanceError = orderValidator.validateDistance(distance);
        if (distanceError.isPresent()) {
            return new ValidatedOrderData(distanceError.get());
        }

        OrderValidator.ValidationResult productValidation =
                orderValidator.validateProducts(request.getItems(), restaurant.getId());
        if (!productValidation.isSuccess()) {
            return new ValidatedOrderData(productValidation.getErrorMessage());
        }

        double total = productValidation.getValidatedItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        int deliveryTime = deliveryCalculator.calculateDeliveryTime(distance);

        return new ValidatedOrderData(
                user,
                restaurant,
                deliveryAddress,
                distance,
                productValidation.getValidatedItems(),
                total,
                deliveryTime);
    }
}
