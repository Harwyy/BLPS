package com.blps.blps.workers.courierFlow;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.repository.OrderRepository;
import com.blps.blps.utils.DistanceCalculator;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ValidateOrderAndSelectBestCourierWorker {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final DistanceCalculator distanceCalculator;

    @JobWorker(type = "validate-order-and-select-best-courier")
    @Transactional
    public Map<String, Object> validateAndSelect(final ActivatedJob job) {
        Map<String, Object> vars = job.getVariablesAsMap();
        Long orderId = ((Number) vars.get("order_id")).longValue();

        Order order = orderRepository.findById(orderId).orElse(null);

        if (order.getStatus() != OrderStatus.READY) {
            throw new BpmnError("BusinessException", String.format("Заказ можно назначить только в статусе READY. Текущий статус: %s", order.getStatus()));
        }

        Address restaurantAddress = order.getRestaurant().getAddress();
        String city = restaurantAddress.getCity();

        List<Courier> availableCouriers = courierRepository.findByCityAndStatus(city, CourierStatus.AVAILABLE)
                .stream()
                .filter(c -> c.getActiveOrdersCount() < 2)
                .collect(Collectors.toList());

        if (availableCouriers.isEmpty()) {
            throw new BpmnError("CourierNotFound", String.format("Нет доступных курьеров в городе %s", city));
        }

        Courier bestCourier = null;
        double bestScore = -1.0;
        for (Courier courier : availableCouriers) {
            double distance = distanceCalculator.calculateDistance(
                    courier.getCurrentLatitude(), courier.getCurrentLongitude(),
                    restaurantAddress.getLatitude(), restaurantAddress.getLongitude());
            double score = calculateScore(courier, distance);
            if (score > bestScore) {
                bestScore = score;
                bestCourier = courier;
            }
        }

        if (bestCourier == null) {
            throw new BpmnError("CourierNotFound", "Не удалось выбрать курьера (расчёт score не дал результата)");
        }

        return Map.of("bestCourier", bestCourier);
    }

    private double calculateScore(Courier courier, double distance) {
        double distanceWeight = 0.5;
        double ratingWeight = 0.3;
        double loadWeight = 0.2;

        double maxDistance = 10.0;
        double distanceScore = Math.max(0, maxDistance - distance) / maxDistance;
        double ratingScore = courier.getRating() / 5.0;
        double loadScore = 1.0 - (courier.getActiveOrdersCount() / 2.0);

        return distanceScore * distanceWeight + ratingScore * ratingWeight + loadScore * loadWeight;
    }
}