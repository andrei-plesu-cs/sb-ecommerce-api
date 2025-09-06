package com.andrei.plesoianu.sbecom.security.jwt;

import com.andrei.plesoianu.sbecom.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtils {
    public static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwt.key}")
    private String jwtSecret;

    @Value("${spring.app.jwt.cookie.name}")
    private String jwtCookie;

    /**
     * Extracts the JWT token from cookie header
     */
    public String getJwtTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * Extracts the JWT token from Authorization header
     */
    public String getJwtTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    /**
     * Generates a cookie containing the Jwt token generated based on the
     * userDetails object (which contains the current authenticated user)
     */
    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails) {
        String jwt = generateTokenFromUsername(userDetails);
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(Duration.ofDays(1))
                .httpOnly(false)
                .build();
    }

    /**
     * Generates a cookie containing no value for the Jwt cookie.
     * Used to clear the cookie.
     */
    public ResponseCookie getCleanJwtToken() {
        return ResponseCookie.from(jwtCookie)
                .path("/api")
                .build();
    }

    /**
     * Extract username from Jwt Token
     */
    public String extractUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    /**
     * Generates signing key
     */
    public Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    /**
     * Validates Jwt Token
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.debug("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.debug("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.debug("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.debug("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Generates token from username
     */
    private String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }
}
