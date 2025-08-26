package com.andrei.plesoianu.sbecom.security.services;

import com.andrei.plesoianu.sbecom.enums.AppRole;
import com.andrei.plesoianu.sbecom.model.Role;
import com.andrei.plesoianu.sbecom.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {
        var authorities = user.getRoles().stream()
                .map(Role::getRoleName)
                .map(AppRole::name)
                .map(SimpleGrantedAuthority::new)
                .toList();

        var userDetailsImpl = new UserDetailsImpl();
        userDetailsImpl.setId(user.getId());
        userDetailsImpl.setUsername(user.getUsername());
        userDetailsImpl.setEmail(user.getEmail());
        userDetailsImpl.setPassword(user.getPassword());
        userDetailsImpl.setAuthorities(authorities);

        return userDetailsImpl;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
