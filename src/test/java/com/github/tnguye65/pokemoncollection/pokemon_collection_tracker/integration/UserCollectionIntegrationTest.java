package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.user.UserRegistrationRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.AddToCollectionRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.UpdateCollectionItemRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.UserCollectionResponse;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service.UserCollectionService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserCollectionIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private UUID testUserId;
    private Long testCollectionId;
    
    private String baseUrl;
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }
    
    @Test
    void testRegisterUserForCollectionTests() {
        // Register a test user first
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("collectiontester");
        request.setEmail("collection@test.com");
        request.setPassword("password123");
        
        ResponseEntity<User> response = restTemplate.postForEntity(
            baseUrl + "/api/users/register", 
            request, 
            User.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        testUserId = response.getBody().getUserId();
        assertNotNull(testUserId);
    }
    
    @Test
    void testAddCardToCollection() {
        testRegisterUserForCollectionTests(); // Ensure user is registered
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId("swsh3-136"); // Known working Furret card
        request.setVariant("normal");
        request.setQuantity(1);
        request.setCondition("mint");
        request.setNotes("Test card addition");
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection",
            request,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        UserCollectionResponse collectionItem = response.getBody();
        testCollectionId = collectionItem.getId();
        
        assertEquals("swsh3-136", collectionItem.getCardId());
        assertEquals("normal", collectionItem.getVariant());
        assertEquals(1, collectionItem.getQuantity());
        assertEquals("mint", collectionItem.getCondition());
        assertEquals("Test card addition", collectionItem.getNotes());
        assertNotNull(collectionItem.getCardDetails());
        assertEquals("Furret", collectionItem.getCardDetails().getName());
    }
    
    @Test
    void testAddDuplicateCardIncrementsQuantity() {
        testRegisterUserForCollectionTests(); // Ensure user is registered
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId("swsh3-136");
        request.setVariant("normal");
        request.setQuantity(1);
        request.setNotes("First addition");
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection",
            request,
            UserCollectionResponse.class
        );

        request.setQuantity(2); // Adding 2 more
        request.setNotes("Additional cards");

        response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection",
            request,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserCollectionResponse collectionItem = response.getBody();
        
        assertEquals(3, collectionItem.getQuantity()); // Should be 1 + 2 = 3
        assertEquals("Additional cards", collectionItem.getNotes()); // Notes should update
    }
    
    @Test
    void testAddDifferentVariantOfSameCard() {
        testAddCardToCollection(); // Ensure base card is added
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId("swsh3-136");
        request.setVariant("reverse"); // Different variant
        request.setQuantity(1);
        request.setCondition("near_mint");
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection",
            request,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserCollectionResponse collectionItem = response.getBody();
        
        assertEquals("reverse", collectionItem.getVariant());
        assertEquals(1, collectionItem.getQuantity()); // Should be separate entry
    }
    
    @Test
    void testGetUserCollection() {
        testAddDifferentVariantOfSameCard(); // Ensure user is registered
        ResponseEntity<List<UserCollectionResponse>> response = restTemplate.exchange(
            baseUrl + "/api/users/" + testUserId + "/collection",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserCollectionResponse>>() {}
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UserCollectionResponse> collection = response.getBody();
        
        assertNotNull(collection);
        assertEquals(2, collection.size()); // Should have 2 items (normal + reverse variants)
        
        // Verify both variants exist
        boolean hasNormal = collection.stream()
            .anyMatch(item -> "normal".equals(item.getVariant()) && item.getQuantity() == 1);
        boolean hasReverse = collection.stream()
            .anyMatch(item -> "reverse".equals(item.getVariant()) && item.getQuantity() == 1);
            
        assertTrue(hasNormal);
        assertTrue(hasReverse);
    }
    
    @Test
    void testUpdateCollectionItem() {
        testAddCardToCollection();
        UpdateCollectionItemRequest request = new UpdateCollectionItemRequest();
        request.setQuantity(5);
        request.setCondition("played");
        request.setNotes("Updated notes");
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.exchange(
            baseUrl + "/api/users/" + testUserId + "/collection/" + testCollectionId,
            HttpMethod.PUT,
            new HttpEntity<>(request),
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserCollectionResponse updated = response.getBody();
        
        assertEquals(5, updated.getQuantity());
        assertEquals("played", updated.getCondition());
        assertEquals("Updated notes", updated.getNotes());
    }
    
    @Test
    void testGetCollectionStats() {
        testAddDifferentVariantOfSameCard();
        ResponseEntity<UserCollectionService.CollectionStats> response = restTemplate.getForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection/stats",
            UserCollectionService.CollectionStats.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserCollectionService.CollectionStats stats = response.getBody();
        
        assertNotNull(stats);
        assertEquals(2, stats.getTotalCards()); // 1 normal + 1 reverse = 2 total
        assertEquals(1, stats.getUniqueCards()); // 1 unique card (Furret)
    }
    
    @Test
    void testDeleteCollectionItem() {
        testAddCardToCollection();
        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl + "/api/users/" + testUserId + "/collection/" + testCollectionId,
            HttpMethod.DELETE,
            null,
            Void.class
        );
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
    
    @Test
    void testAddInvalidCard() {
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId("invalid-card-id");
        request.setVariant("normal");
        request.setQuantity(1);
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection",
            request,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testUnauthorizedCollectionAccess() {
        UUID randomUserId = UUID.randomUUID();
        
        ResponseEntity<List<UserCollectionResponse>> response = restTemplate.exchange(
            baseUrl + "/api/users/" + randomUserId + "/collection",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserCollectionResponse>>() {}
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testValidationErrors() {
        // Test empty card ID
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId("");
        request.setVariant("normal");
        request.setQuantity(1);
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + testUserId + "/collection",
            request,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    void testEmptyCollectionReturnsNoContent() {
        // Register fresh user
        UserRegistrationRequest userRequest = new UserRegistrationRequest();
        userRequest.setUsername("emptyuser");
        userRequest.setEmail("empty@test.com");
        userRequest.setPassword("password123");
        
        ResponseEntity<User> userResponse = restTemplate.postForEntity(
            baseUrl + "/api/users/register", 
            userRequest, 
            User.class
        );
        
        UUID emptyUserId = userResponse.getBody().getUserId();
        
        ResponseEntity<List<UserCollectionResponse>> response = restTemplate.exchange(
            baseUrl + "/api/users/" + emptyUserId + "/collection",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserCollectionResponse>>() {}
        );
        
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}