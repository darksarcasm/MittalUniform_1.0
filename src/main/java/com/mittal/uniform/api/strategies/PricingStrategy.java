package com.mittal.uniform.api.strategies;

import com.mittal.uniform.api.dto.DiscountCalculation;

public interface PricingStrategy {
    DiscountCalculation calculateDiscount(double subtotal);
}
