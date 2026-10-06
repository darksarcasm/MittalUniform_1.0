package com.mittal.uniform.api.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="institute")
public class Institute extends BaseEntity{

    @Column
    private String instituteName;

    @Column
    private String instituteType;

    @Column
    @OneToMany(mappedBy = "institute")
    private List<Product> product;

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public String getInstituteType() {
        return instituteType;
    }

    public void setInstituteType(String instituteType) {
        this.instituteType = instituteType;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}
