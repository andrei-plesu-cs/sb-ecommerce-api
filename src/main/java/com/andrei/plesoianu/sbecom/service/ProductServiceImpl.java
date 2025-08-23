package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.Category;
import com.andrei.plesoianu.sbecom.model.Product;
import com.andrei.plesoianu.sbecom.payload.ProductDto;
import com.andrei.plesoianu.sbecom.payload.ProductResponse;
import com.andrei.plesoianu.sbecom.repositories.CategoryRepository;
import com.andrei.plesoianu.sbecom.repositories.ProductRepository;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ProductServiceImpl implements ProductService {
    private final String basePath;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(@NonNull ProductRepository productRepository,
                              @NonNull CategoryRepository categoryRepository,
                              @NonNull FileService fileService,
                              @NonNull ModelMapper modelMapper,
                              @Value("${project.image}") String basePath) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
        this.basePath = basePath;
    }

    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        var product = new Product();
        product.setProductName(productDto.getProductName());
        product.setImage(productDto.getImage());
        product.setPrice(productDto.getPrice());
        product.setDiscount(productDto.getDiscount());
        product.setQuantity(productDto.getQuantity());
        product.setSpecialPrice(product.computeSpecialPrice());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        var productDtos = productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        return new ProductResponse(productDtos);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        var productDtos = productRepository.findByCategoryOrderByPriceAsc(category).stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();

        return new ProductResponse(productDtos);
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword) {
        var productDtos = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%").stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();

        return new ProductResponse(productDtos);
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto product) {
        var dbProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        dbProduct.setProductName(product.getProductName());
        dbProduct.setImage(product.getImage());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setQuantity(product.getQuantity());
        dbProduct.setSpecialPrice(dbProduct.computeSpecialPrice());

        var updatedProduct = productRepository.save(dbProduct);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public boolean deleteProduct(Long productId) {
        var dbProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        productRepository.delete(dbProduct);
        return true;
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        // Get product from DB
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        // Upload image to server
        // Get the file name of the uploaded image
        Path path = Paths.get(basePath);
        String fileName = fileService.uploadImage(path, image);

        // Update product with image
        product.setImage(fileName);
        var updatedProduct = productRepository.save(product);

        // Return mapping of the product to dto
        return modelMapper.map(updatedProduct, ProductDto.class);
    }
}
