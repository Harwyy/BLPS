package com.blps.blps.service.restaurantServices;

import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.service.OrderService;
import com.blps.blps.service.courierServices.CourierAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantBatchService  {

    private final TransactionTemplate transactionTemplate;
    private final OrderService orderService;
    private final CourierAssignmentService courierAssignmentService;

    public List<RestaurantOrCourierOrderActionResponse> markMultipleOrdersReady(Long restaurantId, List<Long> orderIds) {
        return transactionTemplate.execute(status -> {
            List<RestaurantOrCourierOrderActionResponse> responses = new ArrayList<>();
            try {
                for (Long orderId : orderIds) {
                    Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
                    if (order.getStatus() != OrderStatus.PREPARING) {
                        throw new BusinessException(
                                "Заказ " + orderId + " нельзя отметить как готовый. Текущий статус: " + order.getStatus());
                    }
                    order.setStatus(OrderStatus.READY);
                    orderService.save(order);
                    responses.add(new RestaurantOrCourierOrderActionResponse(
                            order.getId(), order.getStatus().name(), "Заказ готов"));
                }
                return responses;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    public List<RestaurantOrCourierOrderActionResponse> assignCouriersForMultipleOrders(Long restaurantId, List<Long> orderIds) {
        return transactionTemplate.execute(status -> {
            List<RestaurantOrCourierOrderActionResponse> responses = new ArrayList<>();
            try {
                for (Long orderId : orderIds) {
                    Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
                    if (order.getStatus() != OrderStatus.READY) {
                        throw new BusinessException(
                                "Заказ " + orderId + " нельзя назначить курьеру. Текущий статус: " + order.getStatus());
                    }
                    courierAssignmentService.assignCourierToReadyOrderWithinTransaction(orderId);
                    responses.add(new RestaurantOrCourierOrderActionResponse(
                            orderId, OrderStatus.ASSIGNED.name(), "Курьер назначен"));
                }
                return responses;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }
}