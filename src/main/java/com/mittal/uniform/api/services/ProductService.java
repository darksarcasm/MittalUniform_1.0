package com.mittal.uniform.api.services;

import com.mittal.uniform.api.dto.ProductDto;
import com.mittal.uniform.api.dto.ProductFilterRequest;
import com.mittal.uniform.api.dto.ProductVariantDto;
import com.mittal.uniform.api.models.Institute;
import com.mittal.uniform.api.models.Product;
import com.mittal.uniform.api.repositories.ProductRepository;
import com.mittal.uniform.api.utility.SearchUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InstituteService instituteService;

    public ProductService(ProductRepository productRepository, InstituteService instituteService) {
        this.productRepository = productRepository;
        this.instituteService = instituteService;
    }

    @Transactional(readOnly = true)
    public List<Product> searchCatalog(String rawQuery) {
        List<Product> allProducts = productRepository.findAll();

        if (rawQuery == null || rawQuery.trim().isEmpty()) {
            return allProducts;
        }

        String query = rawQuery.trim();

        return allProducts.stream()
                .filter(product ->
                        SearchUtils.isFuzzyMatch(product.getName(), query) ||
                                SearchUtils.isFuzzyMatch(product.getDescription(), query) ||
                                (product.getInstitute() != null && SearchUtils.isFuzzyMatch(product.getInstitute().getInstituteName(), query)) ||
                                // UPGRADED SEARCH LINE: Scans the collection for a match
                                product.getGender() != null &&
                                        SearchUtils.isFuzzyMatch(product.getGender().name(), query))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Slice<Product> getFilteredProducts(ProductFilterRequest criteria) {
        // 1. Configure the database-level sorting parameter
        Sort sort = Sort.unsorted();
        if (criteria.getSortBy() != null) {
            switch (criteria.getSortBy().toLowerCase()) {
                case "price_asc":
                    sort = Sort.by(Sort.Direction.ASC, "variants.price");
                    break;
                case "price_desc":
                    sort = Sort.by(Sort.Direction.DESC, "variants.price");
                    break;
                case "name_asc":
                    sort = Sort.by(Sort.Direction.ASC, "name");
                    break;
            }
        }

        // 2. Fallback protection if page or size are passed as null
        int pageNum = (criteria.getPage() != null) ? criteria.getPage() : 0;
        int pageSize = (criteria.getSize() != null) ? criteria.getPageSize() : 20;

        // 3. Unify pagination and sorting rules into a unified Pageable configuration

        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        // 2. Query the database directly with the active filters
        return productRepository.findByCriteria(
                criteria.getInstituteId(),
                criteria.getGender(),
                criteria.getCategory(),
                criteria.getAgeGroup(),
                criteria.getSize(),
                criteria.getColor(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                pageable
        );
    }

    @Transactional
    public Product addProductToInstitute(Long instituteId, Product product) {
        // 1. Verify the targeted Institute exists
        Institute institute = instituteService.getInstituteById(instituteId);

        // 2. Establish the Unidirectional relationship link
        product.setInstitute(institute);

        // 3. Persist uniform to the inventory catalog
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByInstitute(Long instituteId) {
        List<Product> productList =  productRepository.findByInstituteId(instituteId);

        return productList.stream()
                .map(this::mapToProductDto)
                .toList();
        
    }

    private ProductDto mapToProductDto(Product product) {
        List<ProductVariantDto> variantDtos = product.getVariants().stream()
                .map(variant -> new ProductVariantDto(
                        variant.getId(),
                        variant.getSize(),
                        variant.getPrice(),
                        variant.getStock()
                ))
                .toList();

        return new ProductDto(
                product.getSku(),
                product.getName(),
                product.getDescription(),
                variantDtos
        );
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProductById(Long productId) {
        productRepository.deleteById(productId);
    }
}