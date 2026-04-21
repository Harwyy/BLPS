package com.blps.blps.service.courierServices;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.Courier;
import com.blps.blps.entity.Order;
import com.blps.blps.entity.enums.CourierStatus;
import com.blps.blps.entity.enums.OrderStatus;
import com.blps.blps.exception.BusinessException;
import com.blps.blps.repository.CourierRepository;
import com.blps.blps.service.OrderService;
import com.blps.blps.utils.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourierAssignmentService {

    private final TransactionTemplate transactionTemplate;
    private final OrderService orderService;
    private final CourierRepository courierRepository;
    private final DistanceCalculator distanceCalculator;

    public void assignCourierToReadyOrder(Long orderId) {
        transactionTemplate.execute(status -> {
            try {
                Order order = orderService.getOrderById(orderId);
                if (order.getStatus() != OrderStatus.READY) {
                    throw new BusinessException("Заказ можно назначить только в статусе READY. Текущий статус: " + order.getStatus());
                }

                Courier bestCourier = findBestCourierForOrder(order);
                if (bestCourier == null) {
                    throw new BusinessException("Нет доступных курьеров в городе " + order.getRestaurant().getAddress().getCity());
                }

                bestCourier.setStatus(CourierStatus.BUSY);
                bestCourier.setActiveOrdersCount(bestCourier.getActiveOrdersCount() + 1);
                courierRepository.save(bestCourier);

                order.setCourier(bestCourier);

                order.setStatus(OrderStatus.ASSIGNED);
                order.setAssignedAt(LocalDateTime.now());
                orderService.save(order);
                return null;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    public void assignCourierToReadyOrderWithinTransaction(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order.getStatus() != OrderStatus.READY) {
            throw new BusinessException("Заказ можно назначить только в статусе READY. Текущий статус: " + order.getStatus());
        }
        Courier bestCourier = findBestCourierForOrder(order);
        if (bestCourier == null) {
            throw new BusinessException("Нет доступных курьеров в городе " + order.getRestaurant().getAddress().getCity());
        }
        bestCourier.setStatus(CourierStatus.BUSY);
        bestCourier.setActiveOrdersCount(bestCourier.getActiveOrdersCount() + 1);
        courierRepository.save(bestCourier);
        order.setCourier(bestCourier);
        order.setStatus(OrderStatus.ASSIGNED);
        order.setAssignedAt(LocalDateTime.now());
        orderService.save(order);
    }

    private Courier findBestCourierForOrder(Order order) {
        String city = order.getRestaurant().getAddress().getCity();
        Address restaurantAddress = order.getRestaurant().getAddress();

        List<Courier> availableCouriers = courierRepository.findByCityAndStatus(city, CourierStatus.AVAILABLE)
                .stream()
                .filter(c -> c.getActiveOrdersCount() < 2)
                .collect(Collectors.toList());

        if (availableCouriers.isEmpty()) return null;

        for (Courier courier : availableCouriers) {
            double distance = distanceCalculator.calculateDistance(
                    courier.getCurrentLatitude(), courier.getCurrentLongitude(),
                    restaurantAddress.getLatitude(), restaurantAddress.getLongitude());
            double score = calculateScore(courier, distance);
            courier.setScore(score);
        }
        availableCouriers.sort((c1, c2) -> Double.compare(c2.getScore(), c1.getScore()));
        return availableCouriers.get(0);
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