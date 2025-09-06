package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.payload.address.AddressDto;
import com.andrei.plesoianu.sbecom.service.address.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Address APIs", description = "Endpoints related to the management of user addresses")
@RequestMapping("/api")
public class AddressController {
    private final AddressService addressService;

    public AddressController(@NonNull AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Returns all the addresses")
    @GetMapping("/public/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses() {
        return ResponseEntity.ok(addressService.getAddresses());
    }

    @Operation(
            summary = "Returns the address identified by {addressId}",
            description = "Returns 404 if there is no address with id {addressId}"
    )
    @GetMapping("/public/addresses/{addressId}")
    public ResponseEntity<AddressDto> getAddress(@PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddress(addressId));
    }

    @Operation(summary = "Returns all the addresses of the currently logged in user")
    @GetMapping("/public/user/addresses")
    public ResponseEntity<List<AddressDto>> getUserAddress() {
        return ResponseEntity.ok(addressService.getUserAddresses());
    }

    @Operation(summary = "Creates a new address")
    @PostMapping("/public/address")
    public ResponseEntity<AddressDto> createAddress(@Valid @RequestBody AddressDto addressDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createUserAddress(addressDto));
    }

    @Operation(
            summary = "Updates the address identified by {addressId}",
            description = "Returns 404 if there is no address with id {addressId}"
    )
    @PutMapping("/public/addresses/{addressId}")
    public ResponseEntity<AddressDto> updateAddress(@Valid @RequestBody AddressDto addressDto,
                                                    @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressDto));
    }

    @Operation(
            summary = "Deletes the address identified by {addressId}",
            description = "Returns 404 if there is no address with id {addressId}"
    )
    @DeleteMapping("/public/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
