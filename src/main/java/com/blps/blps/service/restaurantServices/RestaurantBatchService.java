package com.blps.blps.service.restaurantServices;

import com.blps.blps.dto.jms.OrderAssignCourierMessage;
import com.blps.blps.dto.jms.OrderReadyMessage;
import com.blps.blps.dto.response.RestaurantOrCourierOrderActionResponse;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.service.OrderService;
import com.blps.blps.service.courierServices.CourierAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
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

    private final JmsTemplate jmsTemplate;
    private static final String QUEUE_BATCH_READY = "restaurant.batch.ready";
    private static final String QUEUE_ASSIGN_COURIER = "restaurant.batch.assign-courier";

    public void markMultipleOrdersReadyAsync(Long restaurantId, List<Long> orderIds) {
        for (Long orderId : orderIds) {
            OrderReadyMessage message = new OrderReadyMessage(restaurantId, orderId);
            jmsTemplate.convertAndSend(QUEUE_BATCH_READY, message);
        }
    }

    public void assignCouriersForMultipleOrdersAsync(Long restaurantId, List<Long> orderIds) {
        for (Long orderId : orderIds) {
            OrderAssignCourierMessage message = new OrderAssignCourierMessage(restaurantId, orderId);
            jmsTemplate.convertAndSend(QUEUE_ASSIGN_COURIER, message);
        }
    }

//    public List<RestaurantOrCourierOrderActionResponse> assignCouriersForMultipleOrders(Long restaurantId, List<Long> orderIds) {
//        return transactionTemplate.execute(status -> {
//            List<RestaurantOrCourierOrderActionResponse> responses = new ArrayList<>();
//            try {
//                for (Long orderId : orderIds) {
//                    Order order = orderService.getOrderByIdAndRestaurantId(orderId, restaurantId);
//                    if (order.getStatus() != OrderStatus.READY) {
//                        throw new BusinessException(
//                                "Заказ " + orderId + " нельзя назначить курьеру. Текущий статус: " + order.getStatus());
//                    }
//                    courierAssignmentService.assignCourierToReadyOrderWithinTransaction(orderId);
//                    responses.add(new RestaurantOrCourierOrderActionResponse(
//                            orderId, OrderStatus.ASSIGNED.name(), "Курьер назначен"));
//                }
//                return responses;
//            } catch (Exception e) {
//                status.setRollbackOnly();
//                throw e;
//            }
//        });
//    }
}