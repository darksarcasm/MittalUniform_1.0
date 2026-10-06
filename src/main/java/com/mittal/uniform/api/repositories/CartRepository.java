package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
