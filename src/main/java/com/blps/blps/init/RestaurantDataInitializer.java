package com.blps.blps.init;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.repository.AddressRepository;
import com.blps.blps.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(4)
public class RestaurantDataInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Address> addresses = addressRepository.findAll();

        List<Restaurant> restaurants = List.of(
                createRestaurant("Бургерная №1", addresses.get(5), "+74951111111"),
                createRestaurant("Пиццерия Италия", addresses.get(6), "+74952222222"),
                createRestaurant("Суши-бар Япония", addresses.get(7), "+74953333333"),
                createRestaurant("Кофейня Экспресс", addresses.get(8), "+74954444444"),
                createRestaurant("Столовая №5", addresses.get(9), "+74955555555"));

        restaurantRepository.saveAll(restaurants);
    }

    private Restaurant createRestaurant(String name, Address address, String phone) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone(phone);
        return restaurant;
    }
}
