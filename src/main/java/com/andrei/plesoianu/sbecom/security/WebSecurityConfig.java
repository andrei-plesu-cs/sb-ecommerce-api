package com.andrei.plesoianu.sbecom.security;

import com.andrei.plesoianu.sbecom.enums.AppRole;
import com.andrei.plesoianu.sbecom.model.Role;
import com.andrei.plesoianu.sbecom.repositories.RoleRepository;
import com.andrei.plesoianu.sbecom.security.jwt.AuthEntryPointJwt;
import com.andrei.plesoianu.sbecom.security.jwt.AuthTokenFilter;
import com.andrei.plesoianu.sbecom.security.services.UserDetailsServiceImpl;
import lombok.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final RoleRepository roleRepository;

    public WebSecurityConfig(@NonNull UserDetailsServiceImpl userDetailsService,
                             @NonNull AuthEntryPointJwt unauthorizedHandler,
                             @NonNull RoleRepository roleRepository) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.roleRepository = roleRepository;
    }

    @Bean
    public SecurityFilterChain mySecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v2/api-docs/**").permitAll()
                        .requestMatchers("/configuration/ui/**").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/configuration/security/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .authenticationProvider(authenticationProvider())
                // Required for H2 console
                .csrf(CsrfConfigurer::disable) // Disable CSRF for simplicity (or restrict more selectively)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // Allow H2 console to be displayed in frames
                .addFilterBefore(
                    authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData() {
        return (args -> {
           roleRepository.findByRoleName(AppRole.ROLE_USER)
                   .orElseGet(() -> {
                      var role = new Role();
                      role.setRoleName(AppRole.ROLE_USER);
                      return roleRepository.save(role);
                   });

            roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        var role = new Role();
                        role.setRoleName(AppRole.ROLE_ADMIN);
                        return roleRepository.save(role);
                    });

             roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        var role = new Role();
                        role.setRoleName(AppRole.ROLE_SELLER);
                        return roleRepository.save(role);
                    });
        });
    }
}
