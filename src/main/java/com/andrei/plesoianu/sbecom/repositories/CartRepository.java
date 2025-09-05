package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("select c from carts c where c.user.email = ?1")
    Optional<Cart> findByEmail(String email);

    @Query("select c from carts c join fetch c.cartItems ci join fetch ci.product p where p.id = ?1")
    List<Cart> findAllByProductId(Long productId);
}
