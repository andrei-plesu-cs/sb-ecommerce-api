package com.andrei.plesoianu.sbecom.service.order;

import com.andrei.plesoianu.sbecom.payload.order.OrderDto;
import com.andrei.plesoianu.sbecom.payload.order.OrderRequestDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface OrderService {
    @Transactional
    OrderDto placeOrder(OrderRequestDto orderRequestDto, String paymentMethod);

    List<OrderDto> getAllOrders();
}
