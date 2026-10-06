package com.mittal.uniform.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfile extends BaseEntity {

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String preferredPaymentMethod;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }
}
