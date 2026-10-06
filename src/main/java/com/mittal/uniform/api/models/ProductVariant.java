package com.mittal.uniform.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "product_variants")
public class ProductVariant extends BaseEntity {

    @Column// e.g., "32", "34", "M", "L"
    private String size;

    @Column // e.g., "Navy Blue", "White"
    private String color;

    @Column
    private String ageGroup;  // e.g., "Primary", "High School"

    // Price and Stock live here now because they vary by size!
    @Column
    private double price;

    @Column
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Getters and Setters

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
