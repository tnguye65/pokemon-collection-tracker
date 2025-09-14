package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.config.JwtAuthenticationFilter;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.config.JwtService;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserDetailsImpl;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            // Get authenticated user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtService.generateToken(userDetails);

            log.info("User {} logged in successfully", userDetails.getUsername());

            // Return success response
            return ResponseEntity.ok(new AuthResponse(
                    token,
                    "Bearer",
                    userDetails.getUsername(),
                    "Login successful"));

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid email or password"));
        }
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Check if user already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email already exists"));
            }

            // Create new user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setUsername(request.getEmail());

            // Save user to database
            User savedUser = userRepository.save(user);

            // Generate JWT token for automatic login
            String token = jwtService.generateToken(savedUser.getEmail());

            log.info("User {} registered successfully", savedUser.getEmail());

            return ResponseEntity.ok(new AuthResponse(
                    token,
                    "Bearer",
                    savedUser.getEmail(),
                    "Registration successful"));

        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    // DTO Classes
    public static class LoginRequest {
        private String email;
        private String password;

        // Constructors
        public LoginRequest() {
        }

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        private String email;
        private String password;

        // Constructors
        public RegisterRequest() {
        }

        public RegisterRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class AuthResponse {
        private String token;
        private String type;
        private String username;
        private String message;

        public AuthResponse(String token, String type, String username, String message) {
            this.token = token;
            this.type = type;
            this.username = username;
            this.message = message;
        }

        // Getters and Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}