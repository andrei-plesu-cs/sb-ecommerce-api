package com.andrei.plesoianu.sbecom.payload.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;
}
