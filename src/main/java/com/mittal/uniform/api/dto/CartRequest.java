package com.mittal.uniform.api.dto;

public class CartRequest {
    private Long variantId;
    private int quantity;

    public CartRequest() {
    }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
