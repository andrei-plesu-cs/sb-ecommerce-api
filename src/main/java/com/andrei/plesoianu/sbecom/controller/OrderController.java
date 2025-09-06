package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.payload.order.OrderDto;
import com.andrei.plesoianu.sbecom.payload.order.OrderRequestDto;
import com.andrei.plesoianu.sbecom.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Order APIs", description = "Endpoints related to the management of orders")
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    public OrderController(@NonNull OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Creates an order for the currently logged in user",
            description = "{paymentMethod} specifies how the user paid. Eg. \"card\""
    )
    @PostMapping("/public/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderService.placeOrder(orderRequestDto, paymentMethod));
    }

    @Operation(summary = "Returns all the orders")
    @GetMapping("/public/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
