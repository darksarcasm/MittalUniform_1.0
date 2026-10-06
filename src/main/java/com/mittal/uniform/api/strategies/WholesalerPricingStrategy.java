package com.mittal.uniform.api.strategies;

import com.mittal.uniform.api.dto.DiscountCalculation;

public class WholesalerPricingStrategy implements PricingStrategy {

    @Override
    public DiscountCalculation calculateDiscount(double subtotal) {
        double discountPercentage = 0.0;
        double nextTierTarget = 0.0;

        if (subtotal < 10000) {
            discountPercentage = 0.0;
            nextTierTarget = 10000.0;
        } else if (subtotal >= 10000 && subtotal < 30000) {
            discountPercentage = 20.0; // 20% off!
            nextTierTarget = 30000.0;
        } else {
            discountPercentage = 30.0; // Max tier achieved!
            nextTierTarget = 0.0; // No next tier
        }

        double discountAmount = subtotal * (discountPercentage / 100.0);
        double amountNeededForNextTier = nextTierTarget > 0 ? (nextTierTarget - subtotal) : 0.0;

        // Calculate progress percentage for the frontend progress bar UI component
        double progressPercentage = 0.0;
        if (nextTierTarget == 10000.0) {
            progressPercentage = (subtotal / 10000.0) * 100.0;
        } else if (nextTierTarget == 30000.0) {
            // Scale progress smoothly between the 10k and 30k milestone
            progressPercentage = ((subtotal - 10000.0) / 20000.0) * 100.0;
        } else {
            progressPercentage = 100.0; // Maxed out
        }

        return new DiscountCalculation(
                discountPercentage,
                discountAmount,
                amountNeededForNextTier,
                progressPercentage,
                "Order more to save more"
        );
    }
}
