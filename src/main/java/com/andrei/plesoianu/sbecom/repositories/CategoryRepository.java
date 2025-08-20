package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Category;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryName(@NonNull String categoryName);
}
