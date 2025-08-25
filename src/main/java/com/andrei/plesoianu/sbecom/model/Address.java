package com.andrei.plesoianu.sbecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 5)
    private String street;

    @NotBlank
    @Size(min = 5)
    private String buildingName;

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

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();
}
