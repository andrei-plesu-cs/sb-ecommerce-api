package com.andrei.plesoianu.sbecom.service.address;

import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.Address;
import com.andrei.plesoianu.sbecom.payload.address.AddressDto;
import com.andrei.plesoianu.sbecom.repositories.AddressRepository;
import com.andrei.plesoianu.sbecom.util.AuthUtil;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;

    public AddressServiceImpl(@NonNull AuthUtil authUtil,
                              @NonNull ModelMapper modelMapper,
                              @NonNull AddressRepository addressRepository) {
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
    }

    @Override
    public AddressDto createUserAddress(AddressDto addressDto) {
        var user = authUtil.loggedInUser();

        var address = modelMapper.map(addressDto, Address.class);

        user.getAddresses().add(address);
        address.setUser(user);

        var createdAddress = addressRepository.save(address);
        return modelMapper.map(createdAddress, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public AddressDto getAddress(Long addressId) {
        var address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(Address.class, addressId));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getUserAddresses() {
        var username = authUtil.loggedInUsername();
        return addressRepository.findByUsername(username).stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public AddressDto updateAddress(Long addressId, AddressDto addressDto) {
        var address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(Address.class, addressId));

        address.setStreet(addressDto.getStreet());
        address.setStreetNumber(addressDto.getStreetNumber());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setCountry(addressDto.getCountry());
        address.setPincode(addressDto.getPincode());
        var updatedAddress = addressRepository.save(address);

        return modelMapper.map(updatedAddress, AddressDto.class);
    }

    @Override
    public void deleteAddress(Long addressId) {
        if(!addressRepository.existsById(addressId)) {
            throw new NotFoundException(Address.class, addressId);
        }
        addressRepository.deleteById(addressId);
    }
}
