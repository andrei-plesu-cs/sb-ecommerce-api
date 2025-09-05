package com.andrei.plesoianu.sbecom.util;

import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.User;
import com.andrei.plesoianu.sbecom.repositories.UserRepository;
import com.andrei.plesoianu.sbecom.security.services.UserDetailsImpl;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthUtil {
    private final UserRepository userRepository;

    public AuthUtil(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String loggedInEmail() {
        var authentication = extractAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getEmail();
        }
        return null;
    }

    public String loggedInUsername() {
        var authentication = extractAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    public User loggedInUser() {
        var username = loggedInUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(User.class, username));
    }

    private Authentication extractAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Objects.requireNonNull(authentication);
        return authentication;
    }
}
