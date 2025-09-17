package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserCollection;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserCollectionRepository;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;

@Service
@Transactional
public class UserCollectionService {

    private static final Logger log = LoggerFactory.getLogger(UserCollectionService.class);

    @Autowired
    private UserCollectionRepository userCollectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PokemonApiService pokemonApiService;

    /**
     * Add a card to user's collection
     */
    public UserCollection addCardToCollection(UUID userId, String cardId, String variant,
            Integer quantity, String condition, String notes) {
        log.info("Adding card {} ({}) to user {}'s collection", cardId, variant, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Verify card exists in TCGdex API
        try {
            pokemonApiService.getCard(cardId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Card not found in TCGdex API: " + cardId);
        }

        // Check if user already owns this card variant
        Optional<UserCollection> existing = userCollectionRepository
                .findByUserAndCardIdAndVariant(user, cardId, variant);

        if (existing.isPresent()) {
            // Update existing entry
            UserCollection existingCollection = existing.get();
            existingCollection.setQuantity(existingCollection.getQuantity() + quantity);
            if (notes != null) {
                existingCollection.setNotes(notes);
            }
            return userCollectionRepository.save(existingCollection);
        } else {
            // Create new entry
            UserCollection newCollection = new UserCollection();
            newCollection.setUser(user);
            newCollection.setCardId(cardId);
            newCollection.setVariant(variant);
            newCollection.setQuantity(quantity);
            newCollection.setCondition(condition);
            newCollection.setNotes(notes);

            return userCollectionRepository.save(newCollection);
        }
    }

    /**
     * Remove card from user's collection
     */
    public void removeCardFromCollection(UUID userId, Long collectionId) {
        log.info("Removing collection item {} for user {}", collectionId, userId);

        UserCollection collection = userCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection item not found: " + collectionId));

        if (!collection.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Collection item does not belong to user");
        }

        userCollectionRepository.delete(collection);
    }

    /**
     * Update card quantity/condition/notes
     */
    public UserCollection updateCollectionItem(UUID userId, Long collectionId,
            Integer quantity, String condition, String notes) {
        log.info("Updating collection item {} for user {}", collectionId, userId);

        UserCollection collection = userCollectionRepository.findById(collectionId)
                .orElseThrow(() -> new IllegalArgumentException("Collection item not found: " + collectionId));

        if (!collection.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Collection item does not belong to user");
        }

        if (quantity != null)
            collection.setQuantity(quantity);
        if (condition != null)
            collection.setCondition(condition);
        if (notes != null)
            collection.setNotes(notes);

        return userCollectionRepository.save(collection);
    }

    /**
     * Get user's entire collection
     */
    @Transactional(readOnly = true)
    public List<UserCollection> getUserCollection(UUID userId) {
        log.info("Fetching collection for user {}", userId);
        return userCollectionRepository.findByUserUserId(userId);
    }

    /**
     * Get collection statistics
     */
    @Transactional(readOnly = true)
    public CollectionStats getCollectionStats(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Integer totalCards = userCollectionRepository.getTotalCardCount(user);
        Integer uniqueCards = userCollectionRepository.getUniqueCardCount(user);

        return new CollectionStats(totalCards, uniqueCards);
    }

    /**
     * Check if user owns a specific card variant
     */
    @Transactional(readOnly = true)
    public boolean userOwnsCard(UUID userId, String cardId, String variant) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return userCollectionRepository.existsByUserAndCardIdAndVariant(user, cardId, variant);
    }

    /**
     * Inner class for collection statistics
     */
    public static class CollectionStats {
        private final Integer totalCards;
        private final Integer uniqueCards;

        public CollectionStats(Integer totalCards, Integer uniqueCards) {
            this.totalCards = totalCards;
            this.uniqueCards = uniqueCards;
        }

        public Integer getTotalCards() {
            return totalCards;
        }

        public Integer getUniqueCards() {
            return uniqueCards;
        }
    }
}