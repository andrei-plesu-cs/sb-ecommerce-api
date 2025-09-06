package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.enums.QuantityUpdateOperation;
import com.andrei.plesoianu.sbecom.payload.cart.CartDto;
import com.andrei.plesoianu.sbecom.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Cart APIs", description = "Endpoints related to the management of shopping carts")
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;

    public CartController(@NonNull CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Adds product identified by {productId}, with quantity {quantity}, to the cart of the logged in user")
    @PostMapping("/public/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId,
                                                    @PathVariable Integer quantity) {
        CartDto cartDto = cartService.addProductToCart(productId, quantity);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartDto);
    }

    @Operation(summary = "Returns all the carts")
    @GetMapping("/public/carts")
    public ResponseEntity<List<CartDto>> getCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }

    @Operation(summary = "Returns the cart of the logged in user")
    @GetMapping("/public/carts/users/cart")
    public ResponseEntity<CartDto> getUserCart() {
        return ResponseEntity.ok(cartService.getLoggedInUserCart());
    }

    @Operation(
            summary = "Updates the quantity of the product identified by {productId} in the cart of the logged in user",
            description = """
                If the {operation} is INCREASE, the quantity is increased by 1. If the {operation} is DELETE \
                it decreses by 1. If it reaches 0, the product is deleted from the cart"""
    )
    @PutMapping("/public/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDto> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable QuantityUpdateOperation operation) {
        return ResponseEntity.ok(cartService.updateProductQuantityInCart(productId, operation.getNumRepr()));
    }

    @Operation(summary = "Deletes the product identified by {productId} from the cart of the logged in user")
    @DeleteMapping("/public/cart/products/{productId}")
    public ResponseEntity<?> deleteProductFromCart(@PathVariable Long productId) {
        cartService.deleteProductFromCart(productId);
        return ResponseEntity.noContent().build();
    }
}
