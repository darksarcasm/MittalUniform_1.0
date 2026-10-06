package com.mittal.uniform.api.dto;

import com.mittal.uniform.api.models.Status;

public class CheckoutResponse {
    private Long orderId;
    private Double totalAmount;
    private Status status;
    private String gatewayOrderToken; // Mock token representing the payment gateway session

    public CheckoutResponse(Long orderId, Double totalAmount, Status status, String gatewayOrderToken) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.gatewayOrderToken = gatewayOrderToken;
    }

    // Getters...
    public Long getOrderId() { return orderId; }
    public Double getTotalAmount() { return totalAmount; }
    public Status getStatus() { return status; }
    public String getGatewayOrderToken() { return gatewayOrderToken; }
}
