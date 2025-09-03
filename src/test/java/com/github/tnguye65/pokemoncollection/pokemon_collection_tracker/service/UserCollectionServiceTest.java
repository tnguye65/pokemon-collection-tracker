package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCard;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.UserCollection;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserCollectionRepository;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserCollectionServiceTest {
    
    @Mock
    private UserCollectionRepository userCollectionRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PokemonApiService pokemonApiService;
    
    @InjectMocks
    private UserCollectionService userCollectionService;
    
    private UUID testUserId;
    private User testUser;
    private UserCollection testCollection;
    private TCGdexCard testCard;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setUserId(testUserId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        testCollection = new UserCollection();
        testCollection.setId(1L);
        testCollection.setUser(testUser);
        testCollection.setCardId("swsh3-136");
        testCollection.setVariant("normal");
        testCollection.setQuantity(1);
        testCollection.setCondition("mint");
        testCollection.setAddedDate(LocalDateTime.now());
        testCollection.setUpdatedDate(LocalDateTime.now());
        
        testCard = new TCGdexCard();
        testCard.setId("swsh3-136");
        testCard.setName("Furret");
    }
    
    @Test
    void testAddNewCardToCollection() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(pokemonApiService.getCard("swsh3-136")).thenReturn(testCard);
        when(userCollectionRepository.findByUserAndCardIdAndVariant(testUser, "swsh3-136", "normal"))
            .thenReturn(Optional.empty());
        when(userCollectionRepository.save(any(UserCollection.class))).thenReturn(testCollection);
        
        // Act
        UserCollection result = userCollectionService.addCardToCollection(
            testUserId, "swsh3-136", "normal", 1, "mint", "Test notes"
        );
        
        // Assert
        assertNotNull(result);
        assertEquals("swsh3-136", result.getCardId());
        verify(userCollectionRepository).save(any(UserCollection.class));
        verify(pokemonApiService).getCard("swsh3-136");
    }
    
    @Test
    void testAddExistingCardIncrementsQuantity() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(pokemonApiService.getCard("swsh3-136")).thenReturn(testCard);
        when(userCollectionRepository.findByUserAndCardIdAndVariant(testUser, "swsh3-136", "normal"))
            .thenReturn(Optional.of(testCollection));
        when(userCollectionRepository.save(testCollection)).thenReturn(testCollection);
        
        // Act
        UserCollection result = userCollectionService.addCardToCollection(
            testUserId, "swsh3-136", "normal", 2, "mint", "More cards"
        );
        
        // Assert
        assertEquals(3, testCollection.getQuantity()); // 1 + 2 = 3
        assertEquals("More cards", testCollection.getNotes());
        verify(userCollectionRepository).save(testCollection);
    }
    
    @Test
    void testAddCardWithInvalidUser() {
        // Arrange
        UUID invalidUserId = UUID.randomUUID();
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userCollectionService.addCardToCollection(
                invalidUserId, "swsh3-136", "normal", 1, "mint", "Test"
            )
        );
        
        assertEquals("User not found with ID: " + invalidUserId, exception.getMessage());
    }
    
    @Test
    void testAddCardWithInvalidCardId() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(pokemonApiService.getCard("invalid-card")).thenThrow(new RuntimeException("Card not found"));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userCollectionService.addCardToCollection(
                testUserId, "invalid-card", "normal", 1, "mint", "Test"
            )
        );
        
        assertEquals("Card not found in TCGdex API: invalid-card", exception.getMessage());
    }
    
    @Test
    void testGetUserCollection() {
        // Arrange
        List<UserCollection> mockCollection = Arrays.asList(testCollection);
        when(userCollectionRepository.findByUserUserId(testUserId)).thenReturn(mockCollection);
        
        // Act
        List<UserCollection> result = userCollectionService.getUserCollection(testUserId);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(testCollection, result.get(0));
    }
    
    @Test
    void testUpdateCollectionItem() {
        // Arrange
        when(userCollectionRepository.findById(1L)).thenReturn(Optional.of(testCollection));
        when(userCollectionRepository.save(testCollection)).thenReturn(testCollection);
        
        // Act
        UserCollection result = userCollectionService.updateCollectionItem(
            testUserId, 1L, 5, "played", "Updated notes"
        );
        
        // Assert
        assertEquals(5, testCollection.getQuantity());
        assertEquals("played", testCollection.getCondition());
        assertEquals("Updated notes", testCollection.getNotes());
        verify(userCollectionRepository).save(testCollection);
    }
    
    @Test
    void testUpdateCollectionItemUnauthorized() {
        // Arrange
        UUID differentUserId = UUID.randomUUID();
        when(userCollectionRepository.findById(1L)).thenReturn(Optional.of(testCollection));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userCollectionService.updateCollectionItem(differentUserId, 1L, 5, "played", "Notes")
        );
        
        assertEquals("Collection item does not belong to user", exception.getMessage());
    }
    
    @Test
    void testRemoveCardFromCollection() {
        // Arrange
        when(userCollectionRepository.findById(1L)).thenReturn(Optional.of(testCollection));
        
        // Act
        userCollectionService.removeCardFromCollection(testUserId, 1L);
        
        // Assert
        verify(userCollectionRepository).delete(testCollection);
    }
    
    @Test
    void testGetCollectionStats() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userCollectionRepository.getTotalCardCount(testUser)).thenReturn(10);
        when(userCollectionRepository.getUniqueCardCount(testUser)).thenReturn(5);
        
        // Act
        UserCollectionService.CollectionStats stats = userCollectionService.getCollectionStats(testUserId);
        
        // Assert
        assertEquals(10, stats.getTotalCards());
        assertEquals(5, stats.getUniqueCards());
    }
    
    @Test
    void testUserOwnsCard() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userCollectionRepository.existsByUserAndCardIdAndVariant(testUser, "swsh3-136", "normal"))
            .thenReturn(true);
        
        // Act
        boolean owns = userCollectionService.userOwnsCard(testUserId, "swsh3-136", "normal");
        
        // Assert
        assertTrue(owns);
    }
}