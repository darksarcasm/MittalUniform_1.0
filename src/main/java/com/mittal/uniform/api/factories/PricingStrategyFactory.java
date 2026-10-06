package com.mittal.uniform.api.factories;

import com.mittal.uniform.api.models.User;
import com.mittal.uniform.api.models.UserRole;
import com.mittal.uniform.api.strategies.*;
import org.springframework.stereotype.Component;

@Component
public class PricingStrategyFactory {

    public PricingStrategy getStrategy(User user) {
        if (user != null && user.getRoles().contains(UserRole.ROLE_WHOLESALER)) {
            return new WholesalerPricingStrategy();
        }
        // Fallback default for regular retail shoppers or unauthenticated users
        return new CustomerPricingStrategy();
    }
}
