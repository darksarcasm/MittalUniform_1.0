package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Optimized lookup using the Institute's Foreign Key column
    List<Product> findByInstituteId(Long instituteId);

    // DATABASE LEVEL FILTERING ENGINE
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.institute i " +
            "LEFT JOIN p.variants v " +
            "LEFT JOIN p.gender g " +
            "WHERE (:instituteId IS NULL OR i.id = :instituteId) " +
            "AND (:gender IS NULL OR LOWER(g) = LOWER(:gender)) " +
            "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
            "AND (:ageGroup IS NULL OR LOWER(v.ageGroup) = LOWER(:ageGroup)) " +
            "AND (:size IS NULL OR LOWER(v.size) = LOWER(:size)) " +
            "AND (:color IS NULL OR LOWER(v.color) = LOWER(:color)) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice)")
    Slice<Product> findByCriteria(
            @Param("instituteId") Long instituteId,
            @Param("gender") String gender,
            @Param("category") String category,
            @Param("ageGroup") String ageGroup,
            @Param("size") String size,
            @Param("color") String color,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}
