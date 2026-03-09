package com.blps.blps.repository;

import com.blps.blps.entity.Courier;
import com.blps.blps.entity.enums.CourierStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Object> findFirstByStatus(CourierStatus courierStatus);
}
