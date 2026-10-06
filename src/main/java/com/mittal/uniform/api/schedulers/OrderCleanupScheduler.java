package com.mittal.uniform.api.schedulers;

import com.mittal.uniform.api.services.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCleanupScheduler {

    private final OrderService orderService;

    public OrderCleanupScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    // Runs every 15 minutes (900,000 milliseconds)
    @Scheduled(fixedRate = 900000)
    public void releaseAbandonedInventory() {
        try {
            orderService.cancelStalePendingOrders();
        } catch (Exception e) {
            // Keep exceptions isolated so the background thread pool doesn't crash completely
            System.err.println("Error running background inventory cleanup task: " + e.getMessage());
        }
    }
}