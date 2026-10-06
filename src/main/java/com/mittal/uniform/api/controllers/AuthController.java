package com.mittal.uniform.api.controllers;

import com.mittal.uniform.api.authentication.JwtTokenProvider;
import com.mittal.uniform.api.dto.*;
//import com.mittal.uniform.api.models.Address;
import com.mittal.uniform.api.models.User;
import com.mittal.uniform.api.models.UserRole;
import com.mittal.uniform.api.repositories.UserRepository;
import com.mittal.uniform.api.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    // Constructor Injection tracking all security dependencies
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          NotificationService notificationService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 1. Pass incoming raw credentials to the manager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getLoginIdentifier(),
                            loginRequest.getPassword()
                    )
            );

            // 2. If valid, set it into the security context thread locker
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Generate the cryptographically signed JWT token string
            String jwt = tokenProvider.generateToken(authentication);

            // 4. Extract the roles that came from your UserDetailsService DB call
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            // 5. Send back the clean token payload
            return ResponseEntity.ok(new AuthResponse(jwt, loginRequest.getLoginIdentifier(), roles));

        } catch (BadCredentialsException ex) {
            // Spring's manager automatically throws this if email isn't found or password hash mismatches
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // 1. Check if email account already exists in the database
        if (userRepository.findByEmailOrPhoneNumber(registerRequest.getEmail(), registerRequest.getPhone()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Email address is already registered!"));
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setPhoneNumber(registerRequest.getPhone());

        Set<UserRole> assignedRoles = new HashSet<>();
        if (registerRequest.getRoles() == null || registerRequest.getRoles().isEmpty()) {
            assignedRoles.add(UserRole.ROLE_CUSTOMER);
        } else {
            try {
                assignedRoles = registerRequest.getRoles().stream()
                        .map(UserRole::fromString).collect(Collectors.toSet());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", ex.getMessage()));
            }
        }
        user.setRoles(assignedRoles);

        // 2. Map Value Object Address Components from your nested DTO
//        AddressRequest addressDto = registerRequest.getAddressRequest(); // Fetching the nested object
//
//        Address address = new Address();
//        address.setAddressLine1(addressDto.getAddressLine1());
//        address.setAddressLine2(addressDto.getAddressLine2());
//        address.setCity(addressDto.getCity());
//        address.setState(addressDto.getState());
//        address.setPostalCode(addressDto.getPostalCode());

        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap("message", "User registered successfully!"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> triggerPasswordResetOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Email parameter is required."));
        }

        User user = userRepository.findByEmail(email).orElse(null);

        // Security Camouflage: If the user doesn't exist, return a generic success message.
        // This prevents hackers from guessing valid email accounts in your system.
        if (user == null) {
            return ResponseEntity.ok(Collections.singletonMap("message", "If the account exists, an OTP has been sent."));
        }

        // Generate a cryptographically secure 6-digit numeric OTP code
        String generatedOtp = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Set OTP state and a strict 15-minute expiration time window
        user.setResetOtp(generatedOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // TODO: Integrate your email dispatch component here (e.g., emailService.sendOtp(email, generatedOtp))
        System.out.println("SECURITY ACTIONS -> Dispatched OTP [" + generatedOtp + "] to " + email);
        notificationService.sendOtp(user.getEmail(), generatedOtp);

        return ResponseEntity.ok(Collections.singletonMap("message", "If the account exists, an OTP has been sent."));
    }

    // --- 2. VERIFY OTP AND COMMIT NEW PASSWORD ---
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetOrChangePassword(@Valid @RequestBody PasswordChangeRequest changeRequest) {
        User user = userRepository.findByEmail(changeRequest.getEmail()).orElse(null);

        // Fail-fast if user account or transient OTP states don't exist
        if (user == null || user.getResetOtp() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid process state or session expired."));
        }

        // 1. Verify token matching accuracy
        if (!user.getResetOtp().equals(changeRequest.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "The verification OTP code you provided is incorrect."));
        }

        // 2. Enforce chronological expiration rules
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "The verification code has expired. Please request a new one."));
        }

        // 3. Verify matching new password sequence inputs
        if (!changeRequest.getNewPassword().equals(changeRequest.getConfirmNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "New password confirmation inputs do not match."));
        }

        // 4. Defensive Rule: Stop users from choosing their exact current active password
        if (passwordEncoder.matches(changeRequest.getNewPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "New password cannot be identical to your current password."));
        }

        // 5. Encrypt credentials and clear temporary OTP states immediately (Defends against replay attacks)
        user.setPassword(passwordEncoder.encode(changeRequest.getNewPassword()));
        user.setResetOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Your password has been successfully updated!"));
    }
}