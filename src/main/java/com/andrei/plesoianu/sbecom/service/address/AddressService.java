package com.andrei.plesoianu.sbecom.service.address;

import com.andrei.plesoianu.sbecom.payload.address.AddressDto;

import java.util.List;

public interface AddressService {
    AddressDto createUserAddress(AddressDto addressDto);

    List<AddressDto> getAddresses();

    AddressDto getAddress(Long addressId);

    List<AddressDto> getUserAddresses();

    AddressDto updateAddress(Long addressId, AddressDto addressDto);

    void deleteAddress(Long addressId);
}
