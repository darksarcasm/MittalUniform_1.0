package com.mittal.uniform.api.dto;

import java.util.List;

public class ProductDto {
    String sku;
    String name;
    String description;
    List<ProductVariantDto> variants;

    public ProductDto(String sku, String name, String description, List<ProductVariantDto> variants) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.variants = variants;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProductVariantDto> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantDto> variants) {
        this.variants = variants;
    }
}
