package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.payload.address.AddressDto;
import com.andrei.plesoianu.sbecom.service.address.AddressService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {
    private final AddressService addressService;

    public AddressController(@NonNull AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/public/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses() {
        return ResponseEntity.ok(addressService.getAddresses());
    }

    @GetMapping("/public/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddress(addressId));
    }

    @GetMapping("/public/user/addresses")
    public ResponseEntity<List<AddressDto>> getUserAddress() {
        return ResponseEntity.ok(addressService.getUserAddresses());
    }

    @PostMapping("/public/address")
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createUserAddress(addressDto));
    }

    @PutMapping("/public/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@Valid @RequestBody AddressDto addressDto,
                                                    @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressDto));
    }

    @DeleteMapping("/public/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
