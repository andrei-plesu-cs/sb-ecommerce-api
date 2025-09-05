package com.andrei.plesoianu.sbecom.payload.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;

    @NotBlank
    @Size(min = 5)
    private String street;

    @NotBlank
    @Size(min = 2)
    private String streetNumber;

    @NotBlank
    @Size(min = 4)
    private String city;

    @NotBlank
    @Size(min = 2)
    private String state;

    @NotBlank
    @Size(min = 5)
    private String country;

    @NotBlank
    @Size(min = 6)
    private String pincode;
}
