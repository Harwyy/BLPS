package com.blps.blps.entity;

import com.blps.blps.entity.enums.RestaurantStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private RestaurantStatus status = RestaurantStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
