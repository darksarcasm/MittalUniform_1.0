package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.dto.PaymentCallbackRequest;
import com.mittal.uniform.api.models.Order;
import com.mittal.uniform.api.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Server-to-Server Webhook Endpoint
    // POST /api/payments/webhook
    @PostMapping("/webhook")
    public ResponseEntity<String> handlePaymentGatewayCallback(@RequestBody PaymentCallbackRequest callback) {
        try {
            Order updatedOrder = orderService.processPaymentResult(callback);
            return ResponseEntity.ok("Order processing complete. Current state: " + updatedOrder.getStatus());
        } catch (IllegalStateException e) {
            // Return HTTP 200 even if already processed so the gateway stops retrying the webhook
            return ResponseEntity.ok(e.getMessage());
        } catch (Exception e) {
            // Return an error status so the gateway knows to retry sending the notification later
            return ResponseEntity.status(500).body("Internal error processing payment webhook");
        }
    }
}