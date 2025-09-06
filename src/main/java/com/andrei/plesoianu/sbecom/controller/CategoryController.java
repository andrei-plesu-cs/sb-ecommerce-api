package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.config.AppConstants;
import com.andrei.plesoianu.sbecom.enums.SortOrder;
import com.andrei.plesoianu.sbecom.payload.category.CategoryDto;
import com.andrei.plesoianu.sbecom.payload.category.CategoryResponse;
import com.andrei.plesoianu.sbecom.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Category APIs", description = "Endpoints related to the management of categories")
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(@NonNull CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Returns a paginated view of the categories")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.SORT_DIR) SortOrder sortOrder
    ) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));
    }

    @Operation(summary = "Creates a new category")
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDto));
    }

    @Operation(
            summary = "Delete the category identified by {categoryId}",
            description = "Returns 404 if there is no category with id {categoryId}"
    )
    @DeleteMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @Operation(
            summary = "Updated the category identified by {categoryId}",
            description = "Returns 404 if there is no category with id {categoryId}"
    )
    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId,
                                                 @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryDto));
    }
}
