package com.blps.blps.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemDto {

    @NotNull(message = "ID товара обязателен")
    private Long productId;

    @NotNull(message = "Количество обязательно")
    @Positive(message = "Количество должно быть положительным")
    private Integer quantity;

    private String productName;
    private Double price;
}
