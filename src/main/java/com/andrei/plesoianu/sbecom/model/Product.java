package com.andrei.plesoianu.sbecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private String image;

    private String description;

    private Integer quantity;

    private double price;

    private double discount;

    private double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public double computeSpecialPrice() {
        return price - discount * 0.01 * price;
    }
}
