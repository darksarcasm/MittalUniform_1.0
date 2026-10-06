package com.mittal.uniform.api.services;

import com.mittal.uniform.api.dto.CartItemDto;
import com.mittal.uniform.api.dto.CartRequest;
import com.mittal.uniform.api.dto.CartResponseDto;
import com.mittal.uniform.api.models.*;
import com.mittal.uniform.api.repositories.CartItemRepository;
import com.mittal.uniform.api.repositories.CartRepository;
import com.mittal.uniform.api.repositories.ProductVariantRepository;
import com.mittal.uniform.api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository, ProductVariantRepository variantRepository,
                       CartRepository cartRepository,UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCartItems(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(()->{
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });

        List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct(); // Simple navigation

            return new CartItemDto(
                    variant.getId(),
                    product.getName(),
                    product.getSku(),
                    variant.getSize(),
                    variant.getPrice(),
                    item.getQuantity()
            );
        }).toList();

        return new CartResponseDto(cart.getId(), itemDtos,cart.getTotalValue());
    }

    @Transactional
    public CartItem addItemToCart(Long userId, CartRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Cart cart = cartRepository.findByUserId(userId).orElseGet(()->{
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });

        // 1. Check if this exact variant is already inside the user's cart
        return cartItemRepository.findByCartAndProductVariantId(cart, request.getVariantId())
                .map(existingItem -> {
                    // Duplication Guard: Just increment the quantity
                    existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                    return cartItemRepository.save(existingItem);
                })
                .orElseGet(() -> {
                    // Brand new item entry: Fetch variant details and construct a new row
                    ProductVariant variant = variantRepository.findById(request.getVariantId())
                            .orElseThrow(() -> new IllegalArgumentException("Product variant not found"));

                    CartItem newItem = new CartItem();
                    newItem.setProductVariant(variant);
                    newItem.setQuantity(request.getQuantity());
                    return cartItemRepository.save(newItem);
                });
    }

    @Transactional
    public CartItem updateItemQuantity(Long cartItemId, int newQuantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (newQuantity <= 0) {
            cartItemRepository.delete(item);
            return null; // Signals the frontend that the item is completely removed
        }

        item.setQuantity(newQuantity);
        return cartItemRepository.save(item);
    }

    @Transactional
    public void removeItemFromCart(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new IllegalArgumentException("Cart item not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }
}