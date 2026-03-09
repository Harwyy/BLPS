package com.blps.blps.init;

import com.blps.blps.entity.Courier;
import com.blps.blps.repository.CourierRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(3)
public class CourierDataInitializer implements CommandLineRunner {

    private final CourierRepository courierRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Courier> couriers = List.of(
                createCourier("Сергей Васильев", "+79161234567"),
                createCourier("Анна Соколова", "+79172345678"),
                createCourier("Михаил Фёдоров", "+79183456789"),
                createCourier("Ольга Морозова", "+79194567890"),
                createCourier("Денис Новиков", "+79205678901"));

        courierRepository.saveAll(couriers);
    }

    private Courier createCourier(String name, String phone) {
        Courier courier = new Courier();
        courier.setName(name);
        courier.setPhone(phone);
        return courier;
    }
}
