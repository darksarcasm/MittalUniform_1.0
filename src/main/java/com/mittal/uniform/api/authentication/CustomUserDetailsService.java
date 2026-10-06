package com.mittal.uniform.api.authentication;


import com.mittal.uniform.api.models.User;
import com.mittal.uniform.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        // 1. Fetch the user from the database by email
        User user =userRepository.findByEmailOrPhoneNumber(loginIdentifier, loginIdentifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + loginIdentifier));

        // 2. Map their database roles to Spring Security GrantedAuthority objects
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(String.valueOf(role)))
                .collect(Collectors.toList());

        // 3. Return a core Spring Security User object containing the real credentials
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(), // This must be an encrypted BCrypt password string in the DB
                authorities
        );
    }
}