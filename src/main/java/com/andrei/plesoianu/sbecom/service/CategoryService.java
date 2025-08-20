package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.model.Category;
import com.andrei.plesoianu.sbecom.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories();
    void createCategory(Category category);
    String deleteCategory(Long categoryId);
    String updateCategory(Long categoryId, Category category);
}
