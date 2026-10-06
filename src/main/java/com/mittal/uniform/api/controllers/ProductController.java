package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.dto.ProductDto;
import com.mittal.uniform.api.dto.ProductFilterRequest;
import com.mittal.uniform.api.models.Product;
import com.mittal.uniform.api.services.ProductService;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // PUBLIC: Browse all items in the store
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // PUBLIC: Direct catalog filter by target school or workplace
    @GetMapping("/institute/{instituteId}")
    public ResponseEntity<List<ProductDto>> getProductsByInstitute(@PathVariable Long instituteId) {
        return ResponseEntity.ok(productService.getProductsByInstitute(instituteId));
    }

    // SECURED: Only Admin can append items to a specific institute's uniform collection
    @PostMapping("/institute/{instituteId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(
            @PathVariable Long instituteId,
            @RequestBody Product product) {

        Product savedProduct = productService.addProductToInstitute(instituteId, product);
        return ResponseEntity.ok(savedProduct);
    }

    // Accessible via: GET /api/products/search?q=dps bIazer
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("q") String query) {
        List<Product> searchResults = productService.searchCatalog(query);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/filter")
    public ResponseEntity<Slice<Product>> filterProducts(@ModelAttribute ProductFilterRequest criteria) {
        Slice<Product> results = productService.getFilteredProducts(criteria);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        productService.deleteProductById(productId);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
