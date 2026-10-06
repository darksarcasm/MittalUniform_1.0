package com.mittal.uniform.api.dto;

public class DiscountCalculation {
    private double discountPercentage;
    private double discountAmount;
    private double amountNeededForNextTier;
    private double progressPercentage; // e.g., 65.5% filled
    private String customMessage;

    public DiscountCalculation(double discountPercentage, double discountAmount, double amountNeededForNextTier, double progressPercentage, String customMessage) {
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.amountNeededForNextTier = amountNeededForNextTier;
        this.progressPercentage = progressPercentage;
        this.customMessage = customMessage;
    }

    // Multi-argument constructor, Getters...
    public double getDiscountPercentage() { return discountPercentage; }
    public double getDiscountAmount() { return discountAmount; }
    public double getAmountNeededForNextTier() { return amountNeededForNextTier; }
    public double getProgressPercentage() { return progressPercentage; }
}
