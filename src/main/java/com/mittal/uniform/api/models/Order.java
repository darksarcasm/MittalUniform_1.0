package com.mittal.uniform.api.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_profile_id")
    private PartnerUser linkedSchoolContract;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @Column(nullable = false, length = 1000)
    @Embedded
    private OrderAddress shippingAddress;

    private int totalQuantity;

    private double totalAmount;

    Date orderDate;

    Status status;

    public PartnerUser getLinkedSchoolContract() {
        return linkedSchoolContract;
    }

    public void setLinkedSchoolContract(PartnerUser linkedSchoolContract) {
        this.linkedSchoolContract = linkedSchoolContract;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItems(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeVariant(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public OrderAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(OrderAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity() {
        for(OrderItem item : this.items) {
            totalQuantity = totalQuantity + item.getQuantity();
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount() {
        for(OrderItem item : this.items) {
            totalAmount = totalAmount + item.getQuantity() * item.getPrice();
        }
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Embeddable
    public class OrderAddress {

        private String addressLine1;

        private String addressLine2;

        private String city;

        private String state;

        private String postalCode;

        private String addressType;

        public OrderAddress(Address address) {
            this.addressLine1 = address.getAddressLine1();
            this.addressLine2 = address.getAddressLine2();
            this.city = address.getCity();
            this.state = address.getState();
            this.postalCode = address.getPostalCode();
            this.addressType = address.getAddressType();
        }
    }
}
