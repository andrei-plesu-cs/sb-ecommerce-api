package com.andrei.plesoianu.sbecom.payload.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long categoryId;

    @NotBlank(message = "Category name cannot be empty")
    @Size(min = 5, message = "Category name has to be at least 5 characters")
    private String categoryName;
}
