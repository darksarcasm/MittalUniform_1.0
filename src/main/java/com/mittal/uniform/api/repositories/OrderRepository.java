package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.Order;
import com.mittal.uniform.api.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetch a single parent's order history, newest first
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Fetch all system orders by status (for the admin fulfillment dashboard)
    List<Order> findByStatusOrderByCreatedAtDesc(Status status);

    List<Order> findByStatusAndCreatedAtBefore(Status status, LocalDateTime cutoffTime);
}