package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCard;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCardBrief;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.PokemonApiService;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonApiController {

    private final PokemonApiService pokemonApiService;

    @Autowired
    public PokemonApiController(PokemonApiService pokemonApiService) {
        this.pokemonApiService = pokemonApiService;
    }

    // Test endpoint - search cards by name
    @GetMapping("/cards/search")
    public ResponseEntity<List<TCGdexCardBrief>> searchCards(@RequestParam String name) {
        try {
            List<TCGdexCardBrief> response = pokemonApiService.searchCards(name);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Test endpoint - get specific card
    @GetMapping("/cards/{cardId}")
    public ResponseEntity<TCGdexCard> getCard(@PathVariable String cardId) {
        try {
            TCGdexCard card = pokemonApiService.getCard(cardId);
            return ResponseEntity.ok(card);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}