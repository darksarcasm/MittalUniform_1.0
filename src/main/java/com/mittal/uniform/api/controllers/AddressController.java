package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.dto.AddressRequest;
import com.mittal.uniform.api.models.Address;
import com.mittal.uniform.api.services.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // 1. Fetch saved addresses for the profile/checkout view
    // GET /api/users/1/addresses
    @GetMapping
    public ResponseEntity<List<Address>> getAddresses(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    // 2. Add a new delivery destination
    // POST /api/users/1/addresses
    @PostMapping
    public ResponseEntity<Address> addAddress(@PathVariable("userId") Long userId,
                                              @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(userId, request));
    }

    // 3. Explicitly swap default selection status
    // PUT /api/users/1/addresses/5/set-default
    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<Address> setDefault(@PathVariable("userId") Long userId,
                                              @PathVariable("addressId") Long addressId) {
        return ResponseEntity.ok(addressService.setAddressAsDefault(userId, addressId));
    }

    // 4. Remove a location entry from user profile book
    // DELETE /api/users/1/addresses/5
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("userId") Long userId,
                                              @PathVariable("addressId") Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}