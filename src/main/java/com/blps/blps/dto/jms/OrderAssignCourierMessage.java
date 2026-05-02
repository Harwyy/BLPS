package com.blps.blps.dto.jms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderAssignCourierMessage implements Serializable {
    private Long restaurantId;
    private Long orderId;
}
