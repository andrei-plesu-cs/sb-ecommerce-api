package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Category;
import com.andrei.plesoianu.sbecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category);
    List<Product> findByProductNameLikeIgnoreCase(String keyword);
}
