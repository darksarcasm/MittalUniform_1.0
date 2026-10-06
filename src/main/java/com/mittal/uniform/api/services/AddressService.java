package com.mittal.uniform.api.services;

import com.mittal.uniform.api.dto.AddressRequest;
import com.mittal.uniform.api.models.Address;
import com.mittal.uniform.api.models.User;
import com.mittal.uniform.api.repositories.AddressRepository;
import com.mittal.uniform.api.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Address> getUserAddresses(Long userId) {
        return userRepository.findAddressesByUserId(userId);
    }

    @Transactional
    public Address addAddress(Long userId,AddressRequest request ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // If this is the user's first address, force it to be the default
        boolean operationalDefault = request.isDefault() || userRepository.findAddressesByUserId(userId).isEmpty();

        if (operationalDefault) {
            handleDefaultAddressToggle(userId);
        }

        Address address = new Address();
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setAddressType(request.getAddressType());
        address.setDefault(operationalDefault);

        // Link bidirectional relationship via your User helper method
        user.getAddresses().add(address);

        return addressRepository.save(address);
    }

    @Transactional
    public Address setAddressAsDefault(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId,userId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        // Turn off the old default address first
        handleDefaultAddressToggle(userId);

        // Activate the new selection
        address.setDefault(true);

        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Address addressToDelete = addressRepository.findByIdAndUserId(addressId,userId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        user.removeAddress(addressToDelete);

        // Edge Case: If they deleted their default address, assign a new default if options remain
        if (addressToDelete.isDefault()) {
            if (!user.getAddresses().isEmpty()) {
                user.getAddresses().get(0).setDefault(true);
            }
        }

        userRepository.save(user);
    }

    private void handleDefaultAddressToggle(Long userId) {
        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(oldDefault -> {
                    oldDefault.setDefault(false);
                    addressRepository.save(oldDefault);
                });
    }
}