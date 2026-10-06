package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}
