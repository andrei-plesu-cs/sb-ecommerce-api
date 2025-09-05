package com.andrei.plesoianu.sbecom.service.cart;

import com.andrei.plesoianu.sbecom.model.Cart;
import com.andrei.plesoianu.sbecom.payload.cart.CartDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDto addProductToCart(Long productId, Integer quantity);

    List<CartDto> getAllCarts();

    CartDto getLoggedInUserCart();

    @Transactional
    CartDto updateProductQuantityInCart(Long productId, int numRepr);

    void deleteProductFromCart(Long productId);

    Cart getUserCart();
}
