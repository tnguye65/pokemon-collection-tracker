package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.security.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException, ServletException {

        log.info("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader != null ? "Bearer ***" : "null");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No valid Bearer token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.info("Extracted JWT token (length: {})", jwt.length());

            final String userEmail = jwtService.extractUsername(jwt);
            log.info("Extracted username from token: {}", userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("Loading user details for: {}", userEmail);

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.info("Loaded user details: {}", userDetails.getUsername());

                boolean isTokenValid = jwtService.validateToken(jwt, userDetails);
                log.info("Token validation result: {}", isTokenValid);

                if (isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Successfully authenticated user: {}", userEmail);
                } else {
                    log.warn("Token validation failed for user: {}", userEmail);
                }
            } else if (userEmail == null) {
                log.warn("Could not extract username from JWT token");
            } else {
                log.info("User already authenticated: {}",
                        SecurityContextHolder.getContext().getAuthentication().getName());
            }

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage(), e);
            // Clear any partial authentication
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}