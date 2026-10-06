package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.Cart;
import com.mittal.uniform.api.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    // 1. Fetch all items currently in a specific user's cart

    // 2. Check if a specific variant (size/color) already exists in the user's cart
    Optional<CartItem> findByCartAndProductVariantId(Cart cart, Long variantId);

    List<CartItem> findByCart(Cart cart);

}
