package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
