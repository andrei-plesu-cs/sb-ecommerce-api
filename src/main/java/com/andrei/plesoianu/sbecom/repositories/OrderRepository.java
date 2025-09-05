package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
