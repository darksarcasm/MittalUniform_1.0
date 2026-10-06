package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.dto.CheckoutResponse;
import com.mittal.uniform.api.models.Order; // Import Enum
import com.mittal.uniform.api.models.Status;
import com.mittal.uniform.api.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/history")
    public ResponseEntity<List<Order>> getHistory(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(orderService.getUserOrderHistory(userId));
    }

    // Spring automagically converts the incoming "status" string parameter to your Enum type
    @GetMapping("/admin")
    public ResponseEntity<List<Order>> getAdminOrders(@RequestParam("status") Status status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    // Spring automagically converts the incoming "value" string parameter to your Enum type
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<Order> changeStatus(@PathVariable("orderId") Long orderId,
                                              @RequestParam("value") Status status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestParam("userId") Long userId) {
        CheckoutResponse response = orderService.initiateCheckout(userId);
        return ResponseEntity.ok(response);
    }
}