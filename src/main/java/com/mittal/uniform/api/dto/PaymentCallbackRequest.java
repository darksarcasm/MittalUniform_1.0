package com.mittal.uniform.api.dto;

public class PaymentCallbackRequest {
    private Long orderId;
    private String transactionId;
    private String paymentStatus; // e.g., "SUCCESS", "FAILED"

    public PaymentCallbackRequest() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}