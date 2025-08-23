package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.payload.ProductDto;
import com.andrei.plesoianu.sbecom.payload.ProductResponse;
import com.andrei.plesoianu.sbecom.service.ProductService;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(@NonNull ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getALlProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword) {
        return ResponseEntity.ok(productService.searchProductsByKeyword(keyword));
    }

    @PostMapping("/admin/categories/{categoryId}/products")
    public ResponseEntity<ProductDto> addProduct(@PathVariable Long categoryId, @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addProduct(categoryId, productDto));
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId, @RequestBody ProductDto product) {
        return ResponseEntity.ok(productService.updateProduct(productId, product));
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(@PathVariable Long productId,
                                                 @RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(productService.updateProductImage(productId, image));
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }
}
