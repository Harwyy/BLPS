package com.blps.blps.init;

import com.blps.blps.entity.Product;
import com.blps.blps.entity.Restaurant;
import com.blps.blps.repository.ProductRepository;
import com.blps.blps.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(5)
public class ProductDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public void run(String... args) throws Exception {

        List<Restaurant> restaurants = restaurantRepository.findAll();

        List<Product> products = new ArrayList<>();

        products.add(createProduct(restaurants.get(0), "Чизбургер", "Сочная котлета, сыр, соус", 250.00, true));
        products.add(createProduct(restaurants.get(0), "Гамбургер", "Классический бургер", 200.00, true));
        products.add(createProduct(restaurants.get(0), "Картошка фри", "Хрустящая картошка", 120.00, true));
        products.add(createProduct(restaurants.get(0), "Кола", "Напиток", 100.00, true));
        products.add(createProduct(restaurants.get(0), "Бургер с беконом", "Временно недоступен", 320.00, false));

        products.add(createProduct(restaurants.get(1), "Маргарита", "Томатный соус, моцарелла", 450.00, true));
        products.add(createProduct(restaurants.get(1), "Пепперони", "Острая колбаса", 550.00, true));
        products.add(createProduct(restaurants.get(1), "Четыре сыра", "Сливочный вкус", 600.00, true));
        products.add(createProduct(restaurants.get(1), "Гавайская", "Курица, ананас", 500.00, false));
        products.add(createProduct(restaurants.get(1), "Чай", "Горячий напиток", 100.00, true));

        products.add(createProduct(restaurants.get(2), "Филадельфия", "Лосось, сливочный сыр", 400.00, true));
        products.add(createProduct(restaurants.get(2), "Калифорния", "Краб, авокадо", 380.00, true));
        products.add(createProduct(restaurants.get(2), "Унаги маки", "Угорь, соус", 420.00, true));
        products.add(createProduct(restaurants.get(2), "Сет 'Самурай'", "Ассорти роллов", 1200.00, false));
        products.add(createProduct(restaurants.get(2), "Имбирь", "Маринованный", 50.00, true));

        products.add(createProduct(restaurants.get(3), "Капучино", "Классический", 200.00, true));
        products.add(createProduct(restaurants.get(3), "Латте", "С молоком", 220.00, true));
        products.add(createProduct(restaurants.get(3), "Американо", "Черный кофе", 150.00, true));
        products.add(createProduct(restaurants.get(3), "Круассан", "Слоеная выпечка", 120.00, true));
        products.add(createProduct(restaurants.get(3), "Эспрессо", "Крепкий", 130.00, false));

        products.add(createProduct(restaurants.get(4), "Борщ", "Свекла, капуста, мясо", 180.00, true));
        products.add(createProduct(restaurants.get(4), "Пельмени", "Ручная лепка", 250.00, true));
        products.add(createProduct(restaurants.get(4), "Компот", "Вишневый", 80.00, true));
        products.add(createProduct(restaurants.get(4), "Котлета по-киевски", "Сливочное масло внутри", 300.00, false));
        products.add(createProduct(restaurants.get(4), "Пюре картофельное", "Гарнир", 90.00, true));

        productRepository.saveAll(products);
    }

    private Product createProduct(
            Restaurant restaurant, String name, String description, double price, boolean available) {
        Product product = new Product();
        product.setRestaurant(restaurant);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setAvailable(available);
        return product;
    }
}
