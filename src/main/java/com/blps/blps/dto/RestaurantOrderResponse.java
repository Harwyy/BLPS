package com.blps.blps.dto;

import java.util.List;
import lombok.Data;

@Data
public class RestaurantOrderResponse {
    private Long orderId;
    private List<OrderItemDto> items;
}
