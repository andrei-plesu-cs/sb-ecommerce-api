package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.config.SortOrder;
import com.andrei.plesoianu.sbecom.payload.ProductDto;
import com.andrei.plesoianu.sbecom.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto addProduct(Long categoryId, ProductDto productDto);
    ProductResponse getAllProducts(Integer pageSize, Integer pageNumber, String sortBy, SortOrder sortDir);
    ProductResponse getProductsByCategory(Long categoryId, Integer pageSize, Integer pageNumber, String sortBy, SortOrder sortDir);
    ProductResponse searchProductsByKeyword(String keyword, Integer pageSize, Integer pageNumber, String sortBy, SortOrder sortDir);
    ProductDto updateProduct(Long productId, ProductDto product);
    boolean deleteProduct(Long productId);
    ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;
}
