package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.AddToCollectionRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.UpdateCollectionItemRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.UserCollectionResponse;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCard;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserCollection;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.PokemonApiService;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.UserCollectionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/{userId}/collection")
@Validated
public class UserCollectionController {

    private static final Logger log = LoggerFactory.getLogger(UserCollectionController.class);

    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private PokemonApiService pokemonApiService;

    /**
     * Add card to user's collection
     */
    @PostMapping
    public ResponseEntity<UserCollectionResponse> addCardToCollection(
            @PathVariable UUID userId,
            @Valid @RequestBody AddToCollectionRequest request) {

        log.info("POST /api/users/{}/collection - Adding card {}", userId, request.getCardId());

        try {
            UserCollection savedCollection = userCollectionService.addCardToCollection(
                    userId, request.getCardId(), request.getVariant(),
                    request.getQuantity(), request.getCondition(), request.getNotes());

            // Fetch card details for response
            TCGdexCard cardDetails = pokemonApiService.getCard(request.getCardId());

            UserCollectionResponse response = mapToResponse(savedCollection, cardDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Bad request when adding card to collection: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error adding card to collection", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get user's complete collection
     */
    @GetMapping
    public ResponseEntity<List<UserCollectionResponse>> getUserCollection(@PathVariable UUID userId) {
        log.info("GET /api/users/{}/collection", userId);

        try {
            List<UserCollection> collections = userCollectionService.getUserCollection(userId);

            if (collections.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            List<UserCollectionResponse> responses = new ArrayList<>();
            for (UserCollection collection : collections) {
                try {
                    TCGdexCard cardDetails = pokemonApiService.getCard(collection.getCardId());
                    responses.add(mapToResponse(collection, cardDetails));
                } catch (Exception e) {
                    log.warn("Could not fetch details for card {}: {}", collection.getCardId(), e.getMessage());
                    // Add response without card details
                    responses.add(mapToResponse(collection, null));
                }
            }

            return ResponseEntity.ok(responses);

        } catch (IllegalArgumentException e) {
            log.error("Bad request when fetching collection: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching user collection", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update collection item
     */
    @PutMapping("/{collectionId}")
    public ResponseEntity<UserCollectionResponse> updateCollectionItem(
            @PathVariable UUID userId,
            @PathVariable Long collectionId,
            @Valid @RequestBody UpdateCollectionItemRequest request) {

        log.info("PUT /api/users/{}/collection/{}", userId, collectionId);

        try {
            UserCollection updatedCollection = userCollectionService.updateCollectionItem(
                    userId, collectionId, request.getQuantity(),
                    request.getCondition(), request.getNotes());

            TCGdexCard cardDetails = pokemonApiService.getCard(updatedCollection.getCardId());
            UserCollectionResponse response = mapToResponse(updatedCollection, cardDetails);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Bad request when updating collection item: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating collection item", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Remove card from collection
     */
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> removeCardFromCollection(
            @PathVariable UUID userId,
            @PathVariable Long collectionId) {

        log.info("DELETE /api/users/{}/collection/{}", userId, collectionId);

        try {
            userCollectionService.removeCardFromCollection(userId, collectionId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            log.error("Bad request when removing card from collection: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error removing card from collection", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get collection statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<UserCollectionService.CollectionStats> getCollectionStats(@PathVariable UUID userId) {
        log.info("GET /api/users/{}/collection/stats", userId);

        try {
            UserCollectionService.CollectionStats stats = userCollectionService.getCollectionStats(userId);
            return ResponseEntity.ok(stats);

        } catch (IllegalArgumentException e) {
            log.error("Bad request when fetching collection stats: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching collection stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Helper method to map entity to response DTO
     */
    private UserCollectionResponse mapToResponse(UserCollection collection, TCGdexCard cardDetails) {
        return new UserCollectionResponse(
                collection.getId(),
                collection.getCardId(),
                collection.getVariant(),
                collection.getQuantity(),
                collection.getCondition(),
                collection.getNotes(),
                collection.getAddedDate(),
                collection.getUpdatedDate(),
                cardDetails);
    }
}