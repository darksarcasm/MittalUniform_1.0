package com.mittal.uniform.api.dto;

public class CartItemDto {
    Long variantId;
    String productName;
    String sku;
    String size;
    Double price;
    Integer quantity;

    public CartItemDto(Long variantId, String productName, String sku, String size, Double price, Integer quantity) {
        this.variantId = variantId;
        this.productName = productName;
        this.sku = sku;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
