package com.blps.blps.repository;

import com.blps.blps.entity.Courier;
import com.blps.blps.entity.enums.CourierStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    List<Courier> findByCityAndStatus(String city, CourierStatus status);

    List<Courier> findByStatus(CourierStatus status);
}
