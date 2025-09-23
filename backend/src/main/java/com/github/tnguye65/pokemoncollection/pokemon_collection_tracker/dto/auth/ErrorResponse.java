package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.auth;

public class ErrorResponse {
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
