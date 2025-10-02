package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserCollection;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserDetailsImpl;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.PokemonApiService;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.UserCollectionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/collection")
@Validated
public class UserCollectionController {

    private static final Logger log = LoggerFactory.getLogger(UserCollectionController.class);

    @Autowired
    private UserCollectionService userCollectionService;

    @Autowired
    private PokemonApiService pokemonApiService;

    @PostMapping
    public ResponseEntity<UserCollectionResponse> addCardToCollection(
            @Valid @RequestBody AddToCollectionRequest request) {

        User authUser = getAuthenticatedUser();
        if (authUser == null) {
            log.warn("Unauthorized attempt to add card to collection");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("POST /api/collection - Adding card {} for user {}",
                request.getCardId(), authUser.getEmail());

        try {
            UserCollection savedCollection = userCollectionService.addCardToCollection(
                    authUser.getUserId(), request.getCardId(), request.getVariant(),
                    request.getQuantity(), request.getCondition(), request.getNotes());

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

    @GetMapping
    public ResponseEntity<List<UserCollectionResponse>> getUserCollection() {

        User authUser = getAuthenticatedUser();
        if (authUser == null) {
            log.warn("Unauthorized attempt to get user collection");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("GET /api/collection for user {}", authUser.getEmail());

        try {
            List<UserCollection> collections = userCollectionService.getUserCollection(authUser.getUserId());

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

    @PutMapping("/{collectionId}")
    public ResponseEntity<UserCollectionResponse> updateCollectionItem(
            @PathVariable Long collectionId,
            @Valid @RequestBody UpdateCollectionItemRequest request) {

        User authUser = getAuthenticatedUser();
        if (authUser == null) {
            log.warn("Unauthorized attempt to get user collection with ID {}", collectionId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("PUT /api/collection/{} for user {}", collectionId, authUser.getEmail());

        try {
            UserCollection updatedCollection = userCollectionService.updateCollectionItem(
                    authUser.getUserId(), collectionId, request.getQuantity(),
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

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> removeCardFromCollection(@PathVariable Long collectionId) {

        User authUser = getAuthenticatedUser();
        if (authUser == null) {
            log.warn("Unauthorized attempt to delete user collection with ID {}", collectionId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("DELETE /api/collection/{} for user {}", collectionId, authUser.getEmail());

        try {
            userCollectionService.removeCardFromCollection(authUser.getUserId(), collectionId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            log.error("Bad request when removing card from collection: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error removing card from collection", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<UserCollectionService.CollectionStats> getCollectionStats() {

        User authUser = getAuthenticatedUser();
        if (authUser == null) {
            log.warn("Unauthorized attempt to get collection stats");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.info("GET /api/collection/stats for user {}", authUser.getEmail());

        try {
            UserCollectionService.CollectionStats stats = userCollectionService
                    .getCollectionStats(authUser.getUserId());
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
     * Helper method to get the authenticated user from JWT token
     * 
     * @return User object if authenticated, null if not
     */
    private User getAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() &&
                    authentication.getPrincipal() instanceof UserDetailsImpl) {

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                return userDetails.getUser();
            }

            return null;
        } catch (Exception e) {
            log.error("Error getting authenticated user", e);
            return null;
        }
    }

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