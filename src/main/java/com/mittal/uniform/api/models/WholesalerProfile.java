package com.mittal.uniform.api.models;

import jakarta.persistence.*;

@Entity
@Table(name = "wholesaler_profiles")
public class WholesalerProfile extends BaseEntity {
    // To practice microservices, we use a raw userId instead of an object join!
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String companyName;

    @Column
    private String gstNumber;

    @Column// Tax Identification
    private Double creditLimit;

    @Column
    private Double outstandingBalance;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(Double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }
}
