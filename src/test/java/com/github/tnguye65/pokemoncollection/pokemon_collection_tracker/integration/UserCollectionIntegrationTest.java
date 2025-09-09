package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.integration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.AddToCollectionRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection.UserCollectionResponse;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.user.UserRegistrationRequest;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.entity.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
})
class UserCollectionIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private String baseUrl;
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }
    
    @Test
    void testAddCardToCollection() {
        UUID userId = createTestUser("addcard_user", "addcard@test.com");
        
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId("swsh3-136"); 
        request.setVariant("normal");
        request.setQuantity(1);
        request.setCondition("mint");
        request.setNotes("Test card addition");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", userId.toString()); // User can access their own collection
        HttpEntity<AddToCollectionRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + userId + "/collection",
            entity,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testUnauthorizedAccess() {
        UUID userA = createTestUser("userA", "userA@test.com");
        UUID userB = createTestUser("userB", "userB@test.com");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", userA.toString()); // UserA trying to access UserB's collection
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/users/" + userB + "/collection", // UserB's collection
            HttpMethod.GET,
            entity,
            String.class
        );
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Helper methods to reduce duplication
    private UUID createTestUser(String username, String email) {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword("password123");
        
        ResponseEntity<User> response = restTemplate.postForEntity(
            baseUrl + "/api/users/register", 
            request, 
            User.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody().getUserId();
    }

    private Long addCardToUser(UUID userId, String cardId, String variant, int quantity) {
        AddToCollectionRequest request = new AddToCollectionRequest();
        request.setCardId(cardId);
        request.setVariant(variant);
        request.setQuantity(quantity);
        request.setCondition("mint");
        
        ResponseEntity<UserCollectionResponse> response = restTemplate.postForEntity(
            baseUrl + "/api/users/" + userId + "/collection",
            request,
            UserCollectionResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        return response.getBody().getId();
    }
}