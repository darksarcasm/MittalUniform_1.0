package com.mittal.uniform.api.strategies;

import com.mittal.uniform.api.dto.DiscountCalculation;

public class CustomerPricingStrategy implements PricingStrategy {

    @Override
    public DiscountCalculation calculateDiscount(double subtotal) {
        // Standard customers pay flat catalog price
        return new DiscountCalculation(0.0, 0.0, 0.0,0.0,"Spend more to save? Only available for corporate wholesalers!");
    }
}