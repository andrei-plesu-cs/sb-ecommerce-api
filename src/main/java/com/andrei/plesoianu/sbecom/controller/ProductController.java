package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.config.AppConstants;
import com.andrei.plesoianu.sbecom.enums.SortOrder;
import com.andrei.plesoianu.sbecom.payload.product.ProductDto;
import com.andrei.plesoianu.sbecom.payload.product.ProductResponse;
import com.andrei.plesoianu.sbecom.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Tag(name = "Product APIs", description = "Endpoints related to the management of products")
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(@NonNull ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Returns a paginated view of the products")
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getALlProducts(
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.SORT_DIR) SortOrder sortOrder) {
        return ResponseEntity.ok(productService.getAllProducts(pageSize, pageNumber, sortBy, sortOrder));
    }

    @Operation(summary = "Returns a paginated view of the products, filtered by category")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.SORT_DIR) SortOrder sortOrder) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageSize, pageNumber, sortBy, sortOrder));
    }

    @Operation(
            summary = "Returns a paginated view of the products, filtered by a keyword",
            description = "Performs a case insensitive search by product name"
    )
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @PathVariable String keyword,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.SORT_DIR) SortOrder sortOrder) {
        return ResponseEntity.ok(productService.searchProductsByKeyword(keyword, pageSize, pageNumber, sortBy, sortOrder));
    }

    @Operation(summary = "Creates a new product, attached to a given category")
    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDto> addProduct(@PathVariable Long categoryId,
                                                 @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(categoryId, productDto));
    }

    @Operation(
            summary = "Updates the product identified by {productId}",
            description = "Returns 400 if there is no product with id {productId}"
    )
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId,
                                                    @Valid @RequestBody ProductDto product) {
        return ResponseEntity.ok(productService.updateProduct(productId, product));
    }

    @Operation(summary = "Uploads an image for the product identified by {productId}")
    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(@PathVariable Long productId,
                                                 @RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(productService.updateProductImage(productId, image));
    }

    @Operation(
            summary = "Deletes the product identified by {productId}",
            description = "Returns 400 if there is no product with id {productId}"
    )
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }
}
