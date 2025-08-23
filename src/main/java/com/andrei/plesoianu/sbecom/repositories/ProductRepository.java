package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Category;
import com.andrei.plesoianu.sbecom.model.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(@NonNull Category category, Pageable sortAndOrderBy);
    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable sortAndOrderBy);
    boolean existsByProductName(@NonNull String productName);
}
