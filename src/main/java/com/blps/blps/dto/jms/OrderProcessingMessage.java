package com.blps.blps.dto.jms;

import com.blps.blps.dto.AddressDto;
import com.blps.blps.dto.request.OrderItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProcessingMessage implements Serializable {
    private Long orderId;
    private Long userId;
    private Long restaurantId;
    private List<OrderItemRequest> items;
    private AddressDto address;
    private String commentToRestaurant;
    private String commentToCourier;
    private Boolean leaveAtDoor;
}