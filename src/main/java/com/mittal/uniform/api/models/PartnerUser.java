package com.mittal.uniform.api.models;

import jakarta.persistence.*;

import java.util.Date;


@Entity
@Table(name = "partner_profiles")
public class PartnerUser extends BaseEntity{

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String instituteName;

    @Column
    private Date contractStartYear;

    @Column
    private Date contractEndYear;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public Date getContractStartYear() {
        return contractStartYear;
    }

    public void setContractStartYear(Date contractStartYear) {
        this.contractStartYear = contractStartYear;
    }

    public Date getContractEndYear() {
        return contractEndYear;
    }

    public void setContractEndYear(Date contractEndYear) {
        this.contractEndYear = contractEndYear;
    }
}
