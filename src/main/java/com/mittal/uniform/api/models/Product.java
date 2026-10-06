package com.mittal.uniform.api.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product extends BaseEntity{

    @Column(nullable = false)
    private String name; // e.g., "DPS Boys Premium Blazer"

    @Column(length = 1000)
    private String description;

    @Column(unique = true, nullable = false)
    private String sku; // Stock Keeping Unit / Item Code (e.g., "DPS-BLZ-M")

    @Column(nullable = false)
    private String imageUrl;

    // NEW RELATIONSHIP: One Product has Many Variants
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Helpful for grouping uniforms in your store front
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute; // e.g., "Delhi Public School"

    private String category;   // e.g., "Winter Wear", "Sports Uniform", "Formals"

    // Standard Getters and Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Institute getInstitute() {
        return institute;
    }

    public void setInstitute(Institute institute) {
        this.institute = institute;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Gender getGender() {
        return gender;
    }

    public void setGenders(Gender gender) {
        this.gender = gender;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(ProductVariant variant) {
        variants.remove(variant);
        variant.setProduct(null);
    }
}