package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.enums.QuantityUpdateOperation;
import com.andrei.plesoianu.sbecom.payload.cart.CartDto;
import com.andrei.plesoianu.sbecom.service.cart.CartService;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;

    public CartController(@NonNull CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/public/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        CartDto cartDto = cartService.addProductToCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);
    }

    @GetMapping("/public/carts")
    public ResponseEntity<List<CartDto>> getCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @GetMapping("/public/carts/users/cart")
    public ResponseEntity<CartDto> getUserCart() {
        return ResponseEntity.ok(cartService.getLoggedInUserCart());
    }

    @PutMapping("/public/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDto> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable QuantityUpdateOperation operation) {
        return ResponseEntity.ok(cartService.updateProductQuantityInCart(productId, operation.getNumRepr()));
    }

    @DeleteMapping("/public/cart/products/{productId}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable Long productId) {
        cartService.deleteProductFromCart(productId);
        return ResponseEntity.noContent().build();
    }
}
