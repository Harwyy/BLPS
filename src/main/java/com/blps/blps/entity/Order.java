package com.blps.blps.entity;

import com.blps.blps.entity.enums.OrderPaymentStatus;
import com.blps.blps.entity.enums.OrderStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @ManyToOne
    @JoinColumn(name = "delivery_address_id", nullable = false)
    private Address deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "delivery_time")
    private Integer estimatedDeliveryTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private OrderPaymentStatus paymentStatus = OrderPaymentStatus.PENDING;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
