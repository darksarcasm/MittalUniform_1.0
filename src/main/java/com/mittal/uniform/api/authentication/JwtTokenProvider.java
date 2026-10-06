package com.mittal.uniform.api.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    // Generate a secure signing key for HS256 algorithm
    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Set token validity token (e.g., 24 hours in milliseconds)
    private final long jwtExpirationInMs = 86400000;

    // 1. Generate token when a user authenticates successfully
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Extract roles from the authenticated user context
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // Bake authorization tiers directly into payload
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    // 2. Extract Username/Email from the token payload claims
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 3. Extract Roles from the token payload claims
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get(org.springframework.security.core.context.SecurityContextHolder.class.getName() != null ? "roles" : "", List.class);
    }

    // 4. Cryptographically validate token integrity and check expiration
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // In a production system, you would log specific exceptions (ExpiredJwtException, MalformedJwtException, etc.)
            System.err.println("JWT Validation Error: " + ex.getMessage());
        }
        return false;
    }
}