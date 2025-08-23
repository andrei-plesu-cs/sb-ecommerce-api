package com.andrei.plesoianu.sbecom.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long productId;

    @NotBlank(message = "Product name cannot be empty")
    @Size(min = 5, message = "Product name has to be at least 5 characters")
    private String productName;

    private String image;

    @NotNull(message = "Quantity must not be null")
    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantity;

    @Positive(message = "Price must be positive")
    private double price;

    @Min(value = 0, message = "Discount must be positive")
    private double discount;

    private double specialPrice;
}
