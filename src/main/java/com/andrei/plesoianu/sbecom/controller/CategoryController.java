package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.config.AppConstants;
import com.andrei.plesoianu.sbecom.config.SortOrder;
import com.andrei.plesoianu.sbecom.payload.CategoryDto;
import com.andrei.plesoianu.sbecom.payload.CategoryResponse;
import com.andrei.plesoianu.sbecom.service.CategoryService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(@NonNull CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.SORT_DIR) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDto));
    }

    @DeleteMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId,
                                                 @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryDto));
    }
}
