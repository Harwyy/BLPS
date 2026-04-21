package com.blps.blps.service.courierServices;

import com.blps.blps.dto.CourierOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.Address;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;
    private final OrderService orderService;

    @Transactional
    public RestaurantOrCourierOrderActionResponse pickUpOrder(Long orderId, Long courierId) {
        Order order = orderService.getOrderByIdAndCourierId(orderId, courierId);

        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new BusinessException(
                    "Заказ можно забрать только в статусе 'НАЗНАЧЕН'. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.PICKED_UP);
        orderService.save(order);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(), order.getStatus().name(), "Заказ принят курьером");
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse deliverOrder(Long orderId, Long courierId) {
        Order order = orderService.getOrderByIdAndCourierId(orderId, courierId);

        if (order.getStatus() != OrderStatus.PICKED_UP) {
            throw new BusinessException(
                    "Заказ можно доставить только после того, как он был принят курьером. Текущий статус: "
                            + order.getStatus());
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderService.save(order);

        Courier courier = courierRepository
                .findById(courierId)
                .orElseThrow(() -> new ResourceNotFoundException("Курьер не найден"));
        courier.setActiveOrdersCount(courier.getActiveOrdersCount() - 1);
        if (courier.getActiveOrdersCount() < 2) {
            courier.setStatus(CourierStatus.AVAILABLE);
        }
        courierRepository.save(courier);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(), order.getStatus().name(), "Заказ доставлен");
    }

    public List<CourierOrderSummaryDto> getOrdersByStatus(Long courierId, OrderStatus status) {
        List<Order> orders = orderService.getListOfOrdersByCourierIdAndStatus(courierId, status);
        return orders.stream().map(this::toSummaryDto).collect(Collectors.toList());
    }

    private CourierOrderSummaryDto toSummaryDto(Order order) {
        return new CourierOrderSummaryDto(
                order.getId(),
                order.getRestaurant().getName(),
                formatAddress(order.getRestaurant().getAddress()),
                formatAddress(order.getDeliveryAddress()),
                order.getTotalAmount(),
                order.getCommentToCourier(),
                order.getLeaveAtDoor(),
                order.getCreatedAt());
    }

    private String formatAddress(Address address) {
        return String.format("%s, %s %d", address.getCity(), address.getStreet(), address.getBuilding());
    }
}
