package com.blps.blps.util;

import org.springframework.stereotype.Component;

@Component
public class DeliveryCalculator {

    private static final double EARTH_RADIUS = 6371;
    private static final double COURIER_SPEED = 30.0;
    private static final int PREPARATION_TIME = 15;

    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public int calculateDeliveryTime(double distanceKm) {
        return (int) Math.ceil((distanceKm / COURIER_SPEED) * 60 + PREPARATION_TIME);
    }
}
