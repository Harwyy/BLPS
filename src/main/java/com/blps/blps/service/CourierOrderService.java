package com.blps.blps.service;

import com.blps.blps.dto.AddressDto;
import com.blps.blps.dto.CourierOrderResponse;
import com.blps.blps.dto.OrderItemDto;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.mapper.AddressMapper;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourierOrderService {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final AddressMapper addressMapper;

    @Transactional(readOnly = true)
    public List<CourierOrderResponse> getActiveOrders(Long courierId) {
        List<OrderStatus> activeStatuses = List.of(OrderStatus.COURIER_ASSIGNED, OrderStatus.PICKED_UP);
        List<Order> orders = orderRepository.findByCourierIdAndStatusIn(courierId, activeStatuses);
        return orders.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public CourierOrderResponse pickupOrder(Long courierId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));
        if (!order.getCourier().getId().equals(courierId)) {
            throw new BusinessException("Заказ не назначен этому курьеру");
        }
        if (order.getStatus() != OrderStatus.COURIER_ASSIGNED) {
            throw new BusinessException("Невозможно забрать заказ в статусе " + order.getStatus());
        }
        order.setStatus(OrderStatus.PICKED_UP);
        order.setPickedUpAt(LocalDateTime.now());
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    @Transactional
    public CourierOrderResponse deliverOrder(Long courierId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Заказ не найден"));
        if (!order.getCourier().getId().equals(courierId)) {
            throw new BusinessException("Заказ не назначен этому курьеру");
        }
        if (order.getStatus() != OrderStatus.PICKED_UP) {
            throw new BusinessException("Невозможно доставить заказ в статусе " + order.getStatus());
        }
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());

        var courier = order.getCourier();
        courier.setStatus(CourierStatus.AVAILABLE);
        courierRepository.save(courier);

        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    private CourierOrderResponse mapToResponse(Order order) {
        CourierOrderResponse response = new CourierOrderResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus().name());

        AddressDto addressDto = addressMapper.toDto(order.getDeliveryAddress());
        response.setDeliveryAddress(addressDto);

        response.setClientName(order.getUser().getName());
        response.setClientPhone(order.getUser().getPhone());

        response.setEstimatedDeliveryTime(order.getEstimatedDeliveryTime());
        response.setCreatedAt(order.getCreatedAt());
        response.setAssignedAt(order.getAssignedAt());
        response.setPickedUpAt(order.getPickedUpAt());
        response.setDeliveredAt(order.getDeliveredAt());

        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    OrderItemDto dto = new OrderItemDto();
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
