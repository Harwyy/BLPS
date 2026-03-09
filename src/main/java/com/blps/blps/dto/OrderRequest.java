package com.blps.blps.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "ID пользователя обязателен")
    private Long userId;

    @NotNull(message = "ID ресторана обязателен")
    private Long restaurantId;

    @Valid
    private AddressDto newAddress;

    @NotNull(message = "Список позиций не может быть пустым")
    @Size(min = 1, message = "Должна быть хотя бы одна позиция")
    private List<@Valid OrderItemDto> items;
}
