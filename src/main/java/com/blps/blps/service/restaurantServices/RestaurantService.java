package com.blps.blps.service.restaurantServices;

import com.blps.blps.dto.RestaurantOrderSummaryDto;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.exception.ResourceNotFoundException;
import com.blps.blps.mapper.RestaurantOrderSummaryMapper;
import com.blps.blps.repository.RestaurantRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.blps.blps.service.OrderService;
import com.blps.blps.service.courierServices.CourierAssignmentService;
import com.blps.blps.service.courierServices.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final OrderService orderService;
    private final CourierService courierService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderSummaryMapper restaurantOrderSummaryMapper;
    private final CourierAssignmentService courierAssignmentService;

    @Transactional
    public RestaurantOrCourierOrderActionResponse rejectOrder(Long orderId, Long restaurantId) {
        Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(
                    "Заказ можно отклонить только в статусе ОПЛАЧЕН. Текущий статус: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED_BY_REST);
        orderService.save(order);

        return new RestaurantOrCourierOrderActionResponse(
                order.getId(), order.getStatus().name(), "Заказ успешно отклонён");
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse confirmOrder(Long orderId, Long restaurantId) {
        Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("Заказ можно подтвердить только в статусе ОПЛАЧЕН. Текущий статус: " + order.getStatus());
        }
        order.setStatus(OrderStatus.PREPARING);
        orderService.save(order);
        return new RestaurantOrCourierOrderActionResponse(order.getId(), order.getStatus().name(), "Заказ успешно подтверждён");
    }

    @Transactional
    public RestaurantOrCourierOrderActionResponse markOrderReady(Long orderId, Long restaurantId) {
        Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
        if (order.getStatus() != OrderStatus.PREPARING) {
            throw new BusinessException("Заказ можно отметить как готовый только в статусе 'ГОТОВИТСЯ'. Текущий статус: " + order.getStatus());
        }
        order.setStatus(OrderStatus.READY);
        orderService.save(order);

        courierAssignmentService.assignCourierToReadyOrder(orderId);

        return new RestaurantOrCourierOrderActionResponse(order.getId(), order.getStatus().name(), "Заказ готов, курьер назначен");
    }

    public List<RestaurantOrderSummaryDto> getOrdersByStatus(Long restaurantId, OrderStatus status) {
        List<Order> orders = orderService.getListOfOrdersByRestaurantIdAndStatus(restaurantId, status);
        return orders.stream().map(restaurantOrderSummaryMapper::toSummaryDto).collect(Collectors.toList());
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ресторан не найден: " + id));
    }
}
