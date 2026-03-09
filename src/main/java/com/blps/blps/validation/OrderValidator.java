package com.blps.blps.validation;

import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.entity.Product;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.enums.RestaurantStatus;
import com.blps.blps.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final ProductRepository productRepository;
    private static final double MAX_DELIVERY_DISTANCE = 15.0;

    public Optional<String> validateRestaurantStatus(Restaurant restaurant) {
        if (restaurant.getStatus() != RestaurantStatus.ACTIVE) {
            return Optional.of("Ресторан временно не работает");
        }
        return Optional.empty();
    }

    public ValidationResult validateProducts(List<OrderItemDto> items, Long restaurantId) {
        List<OrderItemDto> validatedItems = new ArrayList<>();
        for (OrderItemDto itemDto : items) {
            Product product = productRepository.findById(itemDto.getProductId()).orElse(null);
            if (product == null) {
                return ValidationResult.error("Товар с ID " + itemDto.getProductId() + " не найден");
            }
            if (!product.getRestaurant().getId().equals(restaurantId)) {
                return ValidationResult.error("Товар " + product.getName() + " не принадлежит выбранному ресторану");
            }
            if (!product.isAvailable()) {
                return ValidationResult.error("Товар " + product.getName() + " временно недоступен");
            }
            OrderItemDto validated = new OrderItemDto();
            validated.setProductId(product.getId());
            validated.setProductName(product.getName());
            validated.setQuantity(itemDto.getQuantity());
            validated.setPrice(product.getPrice().doubleValue());
            validatedItems.add(validated);
        }
        return ValidationResult.success(validatedItems);
    }

    public Optional<String> validateDistance(double distance) {
        if (distance > MAX_DELIVERY_DISTANCE) {
            return Optional.of("Адрес доставки находится слишком далеко (макс. " + MAX_DELIVERY_DISTANCE + " км)");
        }
        return Optional.empty();
    }

    public static class ValidationResult {
        private final boolean success;
        private final String errorMessage;
        private final List<OrderItemDto> validatedItems;

        private ValidationResult(boolean success, String errorMessage, List<OrderItemDto> validatedItems) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.validatedItems = validatedItems;
        }

        public static ValidationResult success(List<OrderItemDto> items) {
            return new ValidationResult(true, null, items);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public List<OrderItemDto> getValidatedItems() {
            return validatedItems;
        }
    }
}
