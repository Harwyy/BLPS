package com.blps.blps.init;

import com.blps.blps.entity.Address;
import com.blps.blps.repository.AddressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class AddressDataInitializer implements CommandLineRunner {

    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        double baseLat = 55.7558;
        double baseLon = 37.6173;

        List<Address> addresses = List.of(
                createAddress("Москва", "Тверская", 7, baseLat + 0.001, baseLon - 0.001, "3", "12"),
                createAddress("Москва", "Тверская", 10, baseLat + 0.002, baseLon + 0.002, "1", "5"),
                createAddress("Москва", "Большая Дмитровка", 12, baseLat - 0.001, baseLon + 0.003, "2", "8"),
                createAddress("Москва", "Малая Дмитровка", 5, baseLat + 0.003, baseLon - 0.002, "5", "23"),
                createAddress("Москва", "Петровка", 15, baseLat - 0.002, baseLon - 0.001, "1", "15"),
                createAddress("Москва", "Кузнецкий Мост", 3, baseLat + 0.0015, baseLon + 0.0015, "4", "7"),
                createAddress("Москва", "Неглинная", 8, baseLat - 0.0005, baseLon + 0.0025, "2", "19"),
                createAddress("Москва", "Рождественка", 11, baseLat + 0.0025, baseLon - 0.0015, "6", "31"),
                createAddress("Москва", "Софийка", 4, baseLat - 0.0015, baseLon - 0.0025, "3", "4"),
                createAddress("Москва", "Ильинка", 9, baseLat + 0.0008, baseLon + 0.0008, "1", "10"));

        addressRepository.saveAll(addresses);
    }

    private Address createAddress(
            String city, String street, int building, double lat, double lon, String floor, String apartment) {
        Address address = new Address();
        address.setCity(city);
        address.setStreet(street);
        address.setBuilding(building);
        address.setLatitude(lat);
        address.setLongitude(lon);
        address.setFloor(floor);
        address.setApartment(apartment);
        return address;
    }
}
