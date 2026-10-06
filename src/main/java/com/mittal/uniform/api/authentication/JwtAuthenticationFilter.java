package com.mittal.uniform.api.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extract the token string from the incoming HTTP request header
            String jwt = getJwtFromRequest(request);

            // 2. Validate token integrity and expiration
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // 3. Extract user metadata (Username/Email and Roles) out of the token payload
                String username = tokenProvider.getUsernameFromJWT(jwt);
                List<String> roles = tokenProvider.getRolesFromJWT(jwt);

                // 4. Map string names directly to Spring GrantedAuthority types
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // 5. Build an authenticated context object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Set the user into Spring Security global context holder state
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Log security logging parameters for internal infrastructure monitoring
            System.err.println("Could not set user authentication in security context: " + ex.getMessage());
        }

        // Pass control over to the next security validation item in the filter chain
        filterChain.doFilter(request, response);
    }

    // Helper method to parse the "Authorization: Bearer <token>" structure
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Strip away the "Bearer " prefix string
        }
        return null;
    }
}