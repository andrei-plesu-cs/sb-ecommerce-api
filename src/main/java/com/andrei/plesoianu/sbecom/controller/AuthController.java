package com.andrei.plesoianu.sbecom.controller;

import com.andrei.plesoianu.sbecom.enums.AppRole;
import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.Role;
import com.andrei.plesoianu.sbecom.model.User;
import com.andrei.plesoianu.sbecom.repositories.RoleRepository;
import com.andrei.plesoianu.sbecom.repositories.UserRepository;
import com.andrei.plesoianu.sbecom.security.jwt.JwtUtils;
import com.andrei.plesoianu.sbecom.security.request.LoginRequest;
import com.andrei.plesoianu.sbecom.security.request.SignupRequest;
import com.andrei.plesoianu.sbecom.security.response.UserInfoResponse;
import com.andrei.plesoianu.sbecom.security.response.MessageResponse;
import com.andrei.plesoianu.sbecom.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(@NonNull JwtUtils jwtUtils,
                          @NonNull AuthenticationManager authenticationManager,
                          @NonNull UserRepository userRepository,
                          RoleRepository roleRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        UserInfoResponse userInfoResponse = UserInfoResponse.of(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(userInfoResponse);
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtToken();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already taken"));
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already taken"));
        }

        Set<Role> userRoles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            userRoles.add(extractRole(AppRole.ROLE_USER));
        } else {
            userRoles.addAll(request.getRoles().stream()
                    .map(this::extractRole)
                    .collect(Collectors.toSet()));
        }

        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(userRoles);

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User created"));
    }

    @GetMapping("/username")
    public ResponseEntity<String> currentUserName(Authentication authentication) {
        return authentication != null
                ? ResponseEntity.ok(authentication.getName())
                : ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserDetails(Authentication authentication) {
        if (authentication != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            UserInfoResponse userInfoResponse = UserInfoResponse.of(userDetails);
            return ResponseEntity.ok(userInfoResponse);
        } else { // return empty object if no user is logged in
            return ResponseEntity.noContent().build();
        }
    }

    private Role extractRole(AppRole appRole) {
        return roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new NotFoundException(Role.class, appRole.name()));
    }
}
