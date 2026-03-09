package com.blps.blps.mapper;

import com.blps.blps.dto.OrderInfoResponse;
import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.entity.Order;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderInfoResponseMapper {

    private final AddressMapper addressMapper;

    public OrderInfoResponse mapToOrderInfoResponse(Order order) {
        OrderInfoResponse response = new OrderInfoResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setRestaurantId(order.getRestaurant().getId());
        response.setCourierId(order.getCourier() != null ? order.getCourier().getId() : null);
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount().doubleValue());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setCreatedAt(order.getCreatedAt());
        response.setAssignedAt(order.getAssignedAt());
        response.setPickedUpAt(order.getPickedUpAt());
        response.setDeliveredAt(order.getDeliveredAt());

        response.setDeliveryAddress(addressMapper.toDto(order.getDeliveryAddress()));

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    OrderItemDto dto = new OrderItemDto();
                    dto.setProductId(item.getProduct().getId());
                    dto.setProductName(item.getProduct().getName());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice().doubleValue());
                    return dto;
                })
                .collect(Collectors.toList());
        response.setItems(itemDtos);

        return response;
    }
}
