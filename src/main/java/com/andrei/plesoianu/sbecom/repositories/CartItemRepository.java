package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Cart;
import com.andrei.plesoianu.sbecom.model.CartItem;
import com.andrei.plesoianu.sbecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsCartItemByCartAndProduct(Cart cart, Product product);
    Optional<CartItem> findCartItemByCartAndProduct(Cart cart, Product product);
}

