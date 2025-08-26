package com.andrei.plesoianu.sbecom.security.request;

import com.andrei.plesoianu.sbecom.enums.AppRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50, message = "Username can be at most 50 characters long")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 50, message = "Email can be at most 50 characters long")
    @Email(message = "Value is not a valid email")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long")
    private String password;

    private Set<AppRole> roles = new HashSet<>();
}
