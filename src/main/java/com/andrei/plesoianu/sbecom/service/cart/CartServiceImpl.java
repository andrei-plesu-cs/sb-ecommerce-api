package com.andrei.plesoianu.sbecom.service.cart;

import com.andrei.plesoianu.sbecom.exceptions.ApiException;
import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.Cart;
import com.andrei.plesoianu.sbecom.model.CartItem;
import com.andrei.plesoianu.sbecom.model.Product;
import com.andrei.plesoianu.sbecom.model.User;
import com.andrei.plesoianu.sbecom.payload.cart.CartDto;
import com.andrei.plesoianu.sbecom.repositories.CartItemRepository;
import com.andrei.plesoianu.sbecom.repositories.CartRepository;
import com.andrei.plesoianu.sbecom.repositories.ProductRepository;
import com.andrei.plesoianu.sbecom.repositories.UserRepository;
import com.andrei.plesoianu.sbecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;

    public CartServiceImpl(@NonNull CartRepository cartRepository,
                           @NonNull ProductRepository productRepository,
                           @NonNull CartItemRepository cartItemRepository,
                           @NonNull UserRepository userRepository,
                           @NonNull ModelMapper modelMapper,
                           @NonNull AuthUtil authUtil) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.authUtil = authUtil;
    }

    @Override
    public CartDto addProductToCart(Long productId, Integer quantity) {
        // Find existing cart or create one
        var cart = getUserCart();

        // Retrive Product Details
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        // Perform Validations
        if (cartItemRepository.existsCartItemByCartAndProduct(cart, product)) {
            throw new ApiException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() < quantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less then or equal to the quantity " + product.getQuantity());
        }

        var newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem = cartItemRepository.save(newCartItem);

        cart.getCartItems().add(newCartItem);
        cart.recomputePrices();

        cart = cartRepository.save(cart);

        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public List<CartDto> getAllCarts() {
        var carts = cartRepository.findAll();
        return carts.stream()
                .map(cart -> modelMapper.map(cart, CartDto.class))
                .toList();
    }

    @Override
    public CartDto getLoggedInUserCart() {
        var cart = getUserCart();

        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    @Transactional
    public CartDto updateProductQuantityInCart(Long productId, int numRepr) {
        // Get logged in user cart
        var cart = getUserCart();

        // Get product or throw exception if not found
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        var cartItem = cartItemRepository.findCartItemByCartAndProduct(cart, product)
                .orElseThrow(() -> new NotFoundException(CartItem.class, productId));

        int newQuantity = cartItem.getQuantity() + numRepr;

        if (product.getQuantity() < newQuantity) {
            throw new ApiException("Please, make an order of the " + product.getProductName()
                    + " less then or equal to the quantity " + product.getQuantity());
        }

        if (newQuantity == 0) {
            // Remove product from the cart
            cart.getCartItems().remove(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);

            cart.getCartItems().set(cart.getCartItems().indexOf(cartItem), cartItem);
        }

        cart.recomputePrices();
        cartRepository.save(cart);

        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public void deleteProductFromCart(Long productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(Product.class, productId));

        var cart = getUserCart();

        var cartItem = cartItemRepository.findCartItemByCartAndProduct(cart, product)
                .orElseThrow(() -> new NotFoundException(CartItem.class, productId));

        cart.getCartItems().remove(cartItem);
        cart.recomputePrices();
        cartRepository.save(cart);
    }

    /**
     * Gets existing cart associated with the logged in user, or creates
     * one otherwise
     */
    @Override
    public Cart getUserCart() {
        return cartRepository.findByEmail(authUtil.loggedInEmail())
                .orElseGet(() -> {
                    var user = userRepository.findByUsername(authUtil.loggedInUsername())
                            .orElseThrow(() -> new NotFoundException(User.class, authUtil.loggedInUsername()));
                    // Create cart and return it
                    var cartToCreate = new Cart();
                    cartToCreate.setUser(user);
                    return cartRepository.save(cartToCreate);
                });
    }
}
