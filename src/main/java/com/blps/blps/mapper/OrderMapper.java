package com.blps.blps.mapper;

import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.dto.RestaurantOrderResponse;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.OrderItem;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public RestaurantOrderResponse mapToResponse(Order order) {
        RestaurantOrderResponse response = new RestaurantOrderResponse();
        response.setOrderId(order.getId());

        List<OrderItemDto> items =
                order.getItems().stream().map(this::mapToItemDto).collect(Collectors.toList());
        response.setItems(items);
        return response;
    }

    public OrderItemDto mapToItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice().doubleValue());
        return dto;
    }
}
