package com.andrei.plesoianu.sbecom.util;

import com.andrei.plesoianu.sbecom.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthUtil {
    public String loggedInEmail() {
        var authentication = extractAuthentication();
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getEmail();
    }

    public String loggedInUsername() {
        var authentication = extractAuthentication();
        var userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private Authentication extractAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Objects.requireNonNull(authentication);
        return authentication;
    }
}
