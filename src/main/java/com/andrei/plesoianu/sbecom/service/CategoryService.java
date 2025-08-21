package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.config.SortOrder;
import com.andrei.plesoianu.sbecom.payload.CategoryDto;
import com.andrei.plesoianu.sbecom.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, SortOrder sortOrder);
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto deleteCategory(Long categoryId);
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
}
