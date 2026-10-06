package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.dto.CartItemDto;
import com.mittal.uniform.api.dto.CartRequest;
import com.mittal.uniform.api.dto.CartResponseDto;
import com.mittal.uniform.api.models.CartItem;
import com.mittal.uniform.api.models.Product;
import com.mittal.uniform.api.models.ProductVariant;
import com.mittal.uniform.api.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. Fetch entire cart for a user
    // GET /api/cart?userId=1
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    // 2. Add an item or increment its quantity
    // POST /api/cart/add?userId=1
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@RequestParam("userId") Long userId,
                                              @RequestBody CartRequest request) {
        CartItem updatedItem = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(updatedItem);
    }

    // 3. Update quantity directly (e.g., manual text input or button clicks)
    // PUT /api/cart/update/5?quantity=3
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(@PathVariable("cartItemId") Long cartItemId,
                                                   @RequestParam("quantity") int quantity) {
        CartItem updatedItem = cartService.updateItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(updatedItem);
    }

    // 4. Remove a line item completely
    // DELETE /api/cart/5
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable("cartItemId") Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
