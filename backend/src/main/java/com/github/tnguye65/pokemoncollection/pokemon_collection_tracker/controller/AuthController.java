package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.auth.AuthResponse;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.auth.ErrorResponse;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.auth.LoginRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.auth.RegisterRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.security.jwt.JwtService;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.security.service.MyUserDetailsService;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UserService userService;
    private final MyUserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            User user = userService.findByEmail(request.getEmail()).get();

            // Generate JWT token
            String jwt = jwtService.generateToken(userDetails);

            // Set JWT as HttpOnly cookie
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);    // Prevents JavaScript access
            jwtCookie.setSecure(false);     // Set to true in production with HTTPS
            jwtCookie.setPath("/");         // Available for all paths
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(jwtCookie);

            log.info("User {} logged in successfully", userDetails.getUsername());

            // Return user info (no token in body)
            return ResponseEntity.ok(new AuthResponse(
                    user.getUsername(),
                    user.getEmail(),
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

            log.info("User {} registered successfully", savedUser.getEmail());

            return ResponseEntity.ok(new AuthResponse(
                    savedUser.getUsername(),
                    savedUser.getEmail(),
                    "Registration successful"));

        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // Clear the JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Expire immediately
        response.addCookie(jwtCookie);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logged out successfully");
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Map<String, String>> getCsrfToken(CsrfToken csrfToken) {
        Map<String, String> response = new HashMap<>();
        response.put("token", csrfToken.getToken());
        response.put("headerName", csrfToken.getHeaderName());
        response.put("parameterName", csrfToken.getParameterName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        AuthResponse response = new AuthResponse();
        try {
            // Get the currently authenticated user from Spring Security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getPrincipal().equals("anonymousUser")) {

                response.setMessage("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Cast to UserDetails (your MyUserDetailsService implementation)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername(); // In your case, username is the email

            // Fetch full user info from database
            User user = userService.findByEmail(email).get();

            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setMessage("Current user retrieved successfully");

            // Return user info
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setUsername("");
            response.setEmail("");
            response.setMessage("Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}