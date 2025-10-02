package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Catches deserialization errors from incoming request bodies
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof JsonMappingException jme) {
            return ResponseEntity.badRequest().body("JSON parse error: " + jme.getOriginalMessage() +
                    " at path: " + jme.getPathReference());
        }
        return ResponseEntity.badRequest().body("Invalid request JSON: " + ex.getMessage());
    }

    // Catches errors when RestTemplate/Jackson fails on external API responses
    @ExceptionHandler(org.springframework.http.converter.HttpMessageConversionException.class)
    public ResponseEntity<String> handleHttpMessageConversion(org.springframework.http.converter.HttpMessageConversionException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof JsonMappingException jme) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Failed to map API response: " + jme.getOriginalMessage() +
                          " at path: " + jme.getPathReference());
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("Failed to process API response: " + ex.getMessage());
    }
}
