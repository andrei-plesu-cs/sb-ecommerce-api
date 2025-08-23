package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.payload.ProductDto;
import com.andrei.plesoianu.sbecom.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto addProduct(Long categoryId, ProductDto productDto);
    ProductResponse getAllProducts();
    ProductResponse getProductsByCategory(Long categoryId);
    ProductResponse searchProductsByKeyword(String keyword);
    ProductDto updateProduct(Long productId, ProductDto product);
    boolean deleteProduct(Long productId);
    ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;
}
