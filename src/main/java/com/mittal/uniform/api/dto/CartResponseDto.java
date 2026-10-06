package com.mittal.uniform.api.dto;

import java.util.List;

public class CartResponseDto {

    Long cartId;
    List<CartItemDto> items;
    Double totalAmount;

    public CartResponseDto(Long cartId, List<CartItemDto> items, Double totalAmount) {
        this.cartId = cartId;
        this.items = items;
        this.totalAmount = totalAmount;
    }
}
