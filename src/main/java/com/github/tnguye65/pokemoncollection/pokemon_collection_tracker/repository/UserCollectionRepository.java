package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserCollection;

@Repository
public interface UserCollectionRepository extends JpaRepository<UserCollection, Long> {

    /**
     * Find all collection items for a specific user
     */
    List<UserCollection> findByUser(User user);

    /**
     * Find all collection items for a user by user ID
     */
    List<UserCollection> findByUserUserId(UUID userId);

    /**
     * Find a specific card in user's collection
     */
    Optional<UserCollection> findByUserAndCardIdAndVariant(User user, String cardId, String variant);

    /**
     * Find all variants of a specific card owned by user
     */
    List<UserCollection> findByUserAndCardId(User user, String cardId);

    /**
     * Check if user owns a specific card variant
     */
    boolean existsByUserAndCardIdAndVariant(User user, String cardId, String variant);

    /**
     * Get total number of cards in user's collection
     */
    @Query("SELECT COALESCE(SUM(uc.quantity), 0) FROM UserCollection uc WHERE uc.user = :user")
    Integer getTotalCardCount(@Param("user") User user);

    /**
     * Get total number of unique cards (ignoring quantities and variants)
     */
    @Query("SELECT COUNT(DISTINCT uc.cardId) FROM UserCollection uc WHERE uc.user = :user")
    Integer getUniqueCardCount(@Param("user") User user);

    /**
     * Find collection items by condition
     */
    List<UserCollection> findByUserAndCondition(User user, String condition);

    /**
     * Delete all collection items for a user
     */
    void deleteByUser(User user);
}