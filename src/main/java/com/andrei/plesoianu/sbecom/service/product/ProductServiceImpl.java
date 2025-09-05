package com.andrei.plesoianu.sbecom.service.product;

import com.andrei.plesoianu.sbecom.enums.SortOrder;
import com.andrei.plesoianu.sbecom.exceptions.ApiException;
import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.Cart;
import com.andrei.plesoianu.sbecom.model.Category;
import com.andrei.plesoianu.sbecom.model.Product;
import com.andrei.plesoianu.sbecom.payload.product.ProductDto;
import com.andrei.plesoianu.sbecom.payload.product.ProductResponse;
import com.andrei.plesoianu.sbecom.repositories.CartItemRepository;
import com.andrei.plesoianu.sbecom.repositories.CartRepository;
import com.andrei.plesoianu.sbecom.repositories.CategoryRepository;
import com.andrei.plesoianu.sbecom.repositories.ProductRepository;
import com.andrei.plesoianu.sbecom.service.file.FileService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final String basePath;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(@NonNull ProductRepository productRepository,
                              @NonNull CategoryRepository categoryRepository,
                              @NonNull CartRepository cartRepository,
                              @NonNull CartItemRepository cartItemRepository,
                              @NonNull FileService fileService,
                              @NonNull ModelMapper modelMapper,
                              @Value("${project.image}") String basePath) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
        this.basePath = basePath;
    }

    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        // Check if there is already one product with the same name
        if (productRepository.existsByProductName(productDto.getProductName())) {
            throw new ApiException("A product with name %s already exists".formatted(productDto.getProductName()));
        }

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
    public ProductResponse getAllProducts(Integer pageSize, Integer pageNumber, String sortBy, SortOrder sortDir) {
        Sort sortByAndOrder = switch (sortDir) {
            case ASCENDING ->  Sort.by(Sort.Direction.ASC, sortBy);
            case DESCENDING ->  Sort.by(Sort.Direction.DESC, sortBy);
        };

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        var productPage = productRepository.findAll(pageDetails);

        return constructResponseFromPage(productPage);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageSize, Integer pageNumber,
                                                 String sortBy, SortOrder sortDir) {
        Sort sortByAndOrder = switch (sortDir) {
            case ASCENDING ->  Sort.by(Sort.Direction.ASC, sortBy);
            case DESCENDING ->  Sort.by(Sort.Direction.DESC, sortBy);
        };

        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        var productPage = productRepository.findByCategory(category, pageDetails);

        return constructResponseFromPage(productPage);
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword, Integer pageSize, Integer pageNumber,
                                                   String sortBy, SortOrder sortDir) {
        Sort sortByAndOrder = switch (sortDir) {
            case ASCENDING ->  Sort.by(Sort.Direction.ASC, sortBy);
            case DESCENDING ->  Sort.by(Sort.Direction.DESC, sortBy);
        };

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        var productPage = productRepository.findByProductNameLikeIgnoreCase("%" + keyword + "%", pageDetails);

        return constructResponseFromPage(productPage);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long productId, ProductDto product) {
        var dbProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        dbProduct.setProductName(product.getProductName());
        dbProduct.setImage(product.getImage());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setDiscount(product.getDiscount());
        dbProduct.setQuantity(product.getQuantity());
        dbProduct.setSpecialPrice(dbProduct.computeSpecialPrice());

        List<Cart> carts = cartRepository.findAllByProductId(productId);
        for (Cart cart: carts) {
            var cartItem = cart.findCartItemByProductId(productId);
            if (cartItem != null) {
                cartItem.setProductPrice(dbProduct.getSpecialPrice());
                cartItem.setDiscount(dbProduct.getDiscount());
                cartItemRepository.save(cartItem);
                cart.recomputePrices();
            }
        }
        cartRepository.saveAll(carts);

        var updatedProduct = productRepository.save(dbProduct);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long productId) {
        var dbProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        List<Cart> carts = cartRepository.findAllByProductId(productId);
        for (Cart cart: carts) {
            var cartItem = cart.findCartItemByProductId(productId);
            if (cartItem != null) {
                cart.getCartItems().remove(cartItem);
                cart.recomputePrices();
            }
        }
        cartRepository.saveAll(carts);

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

    private ProductResponse constructResponseFromPage(Page<Product> productPage) {
        var response = new ProductResponse();
        response.setContent(productPage.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList());
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());
        return response;
    }
}
