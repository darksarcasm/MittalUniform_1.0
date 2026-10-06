package com.mittal.uniform.api.services;

import com.mittal.uniform.api.dto.CheckoutResponse;
import com.mittal.uniform.api.dto.PaymentCallbackRequest;
import com.mittal.uniform.api.models.*;
import com.mittal.uniform.api.repositories.CartRepository;
import com.mittal.uniform.api.repositories.OrderRepository;
import com.mittal.uniform.api.repositories.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, ProductVariantRepository variantRepository,
                        CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.variantRepository = variantRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> getUserOrderHistory(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Updated to accept OrderStatus Enum
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Status status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    // Updated to accept OrderStatus Enum
    @Transactional
    public Order updateOrderStatus(Long orderId, Status newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Business Rule Guard: Enums allow compile-time checked equality operations
        if (order.getStatus() == Status.CANCELLED || order.getStatus() == Status.DELIVERED) {
            throw new IllegalStateException("Cannot change status of a completed or cancelled order");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order processPaymentResult(PaymentCallbackRequest callback) {
        Order order = orderRepository.findById(callback.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order record matching ID not found"));

        // Guard Clause: Prevent processing payments for orders already finalized
        if (order.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Order is already processed and is currently: " + order.getStatus());
        }

        if ("SUCCESS".equalsIgnoreCase(callback.getPaymentStatus())) {
            order.setStatus(Status.PAID);
            // Transaction complete! Stock was already decremented during checkout creation.
        } else {
            order.setStatus(Status.CANCELLED);

            // PAYMENT FAILED: Safely release inventory stock back to the catalog
            rollbackOrderInventory(order);
        }

        return orderRepository.save(order);
    }

    private void rollbackOrderInventory(Order order) {
        if (order.getItems() == null) return;

        for (var item : order.getItems()) {
            var variant = item.getProductVariant();
            if (variant != null) {
                // Restore the original stock count
                variant.setStock(variant.getStock() + item.getQuantity());
                // Since productVariantRepository is injected, we save the updated inventory row
                variantRepository.save(variant);
            }
        }
    }

    @Transactional
    public CheckoutResponse initiateCheckout(Long userId) {
        // 1. Fetch the user's active cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found for user"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart");
        }

        // 2. Create a new Order shell
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(Status.PENDING);

        double totalAmount = 0.0;

        // 3. Convert CartItems into fixed OrderItems & validate stock levels
        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            Product product = variant.getProduct();

            // Business Rule Guard: Double check inventory stock count before taking money
            if (variant.getStock() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for item: "
                        + product.getName() + " (Size: " + variant.getSize() + ")");
            }

            // Deduct inventory stock immediately to hold the items during payment window
            variant.setStock(variant.getStock() - cartItem.getQuantity());
            variantRepository.save(variant);

            // Build the immutable order line item row
            OrderItem orderItem = new OrderItem();
            orderItem.setProductVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(variant.getPrice()); // Snapshot the current price permanently

            order.addItems(orderItem); // Helper method to link bidirectional child row

            totalAmount += (variant.getPrice() * cartItem.getQuantity());
        }

        order.setTotalAmount();
        Order savedOrder = orderRepository.save(order);

        // 4. Clear out the database Cart records so they don't see items during payment
        cart.getItems().clear();
        cartRepository.save(cart);

        // 5. Generate a mock external gateway tracking token (e.g., simulating Stripe/Razorpay)
        String mockGatewayToken = "pay_tok_" + java.util.UUID.randomUUID().toString().substring(0, 8);

        return new CheckoutResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus(),
                mockGatewayToken
        );
    }

    @Transactional
    public void cancelStalePendingOrders() {
        // Look back 30 minutes from this exact moment
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);

        List<Order> staleOrders = orderRepository.findByStatusAndCreatedAtBefore(Status.PENDING, cutoffTime);

        if (staleOrders.isEmpty()) {
            return; // Nothing to clean up right now
        }

        for (Order order : staleOrders) {
            order.setStatus(Status.CANCELLED);

            // Loop through the locked line items and restore the catalog stock counters
            for (var item : order.getItems()) {
                var variant = item.getProductVariant();
                if (variant != null) {
                    variant.setStock(variant.getStock() + item.getQuantity());
                    variantRepository.save(variant);
                }
            }

            orderRepository.save(order);
        }

        System.out.println("Cleaned up " + staleOrders.size() + " abandoned pending checkouts.");
    }
}
