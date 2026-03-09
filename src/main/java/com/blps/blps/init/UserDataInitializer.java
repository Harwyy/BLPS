package com.blps.blps.init;

import com.blps.blps.entity.Address;
import com.blps.blps.entity.User;
import com.blps.blps.repository.AddressRepository;
import com.blps.blps.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Address> addresses = addressRepository.findAll();

        List<User> users = List.of(
                createUser("Иван Петров", "ivan.petrov@example.com", "+79011234567", addresses.get(0)),
                createUser("Мария Смирнова", "maria.smirnova@example.com", "+79022345678", addresses.get(1)),
                createUser("Алексей Иванов", "alexey.ivanov@example.com", "+79033456789", addresses.get(2)),
                createUser("Елена Кузнецова", "elena.kuznetsova@example.com", "+79044567890", addresses.get(3)),
                createUser("Дмитрий Попов", "dmitry.popov@example.com", "+79055678901", addresses.get(4)));

        userRepository.saveAll(users);
    }

    private User createUser(String name, String email, String phone, Address address) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        return user;
    }
}
