package com.mittal.uniform.api.repositories;

import com.mittal.uniform.api.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
