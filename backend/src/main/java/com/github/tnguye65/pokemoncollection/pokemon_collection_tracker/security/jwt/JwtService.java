package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret:pRLnXHVoP09y0PZo7ECBfY0fLx5a2xu2}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Object extractCustomClaim(String token, String claimName) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimName);
    }

    public String generateToken(Map<String, Object> extraClaims, String userName, long expireInterval) {
        try {
            return Jwts.builder()
                    .claims().add(extraClaims)
                    .subject(userName)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expireInterval))
                    .and()
                    .signWith(getSignInKey())
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating token for user: {}", userName, e);
            throw new RuntimeException("Could not generate token", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails.getUsername(), jwtExpiration);
    }

    public String generateToken(String username) {
        return generateToken(new HashMap<>(), username, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return generateToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return generateToken(claims, username, refreshExpiration);
    }

    private Claims extractAllClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        if (userDetails == null) {
            return false;
        }
        return validateToken(token, userDetails.getUsername());
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            boolean isValid = (extractedUsername.equals(username)) && !isTokenExpired(token);

            if (isValid) {
                logger.debug("Token validated successfully for user: {}", username);
            } else {
                logger.warn("Token validation failed for user: {}", username);
            }

            return isValid;
        } catch (Exception e) {
            logger.error("Error validating token for user: {}", username, e);
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Object tokenType = extractCustomClaim(token, "type");
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSignInKey() {
        // Handle the secret key properly - if it's already base64 encoded, decode it
        // Otherwise, use it as UTF-8 bytes
        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secretKey);
        } catch (IllegalArgumentException e) {
            // If decoding fails, treat as regular string and encode to bytes
            keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true; // Consider expired if we can't check
        }
    }

    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            logger.error("Error getting token remaining time: {}", e.getMessage());
            return 0;
        }
    }

    public boolean isTokenExpiringSoon(String token, long thresholdMs) {
        long remainingTime = getTokenRemainingTime(token);
        return remainingTime > 0 && remainingTime < thresholdMs;
    }

    // Utility method to check if token needs refresh (expires within 5 minutes)
    public boolean needsRefresh(String token) {
        return isTokenExpiringSoon(token, 5 * 60 * 1000); // 5 minutes
    }
}