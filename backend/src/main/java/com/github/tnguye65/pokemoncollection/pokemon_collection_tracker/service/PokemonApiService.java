package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.config.TCGdexApiProperties;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCard;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCardBrief;

@Service
public class PokemonApiService {

    private static final Logger logger = LoggerFactory.getLogger(PokemonApiService.class);

    private final RestTemplate restTemplate;
    private final TCGdexApiProperties apiProperties;

    @Autowired
    public PokemonApiService(RestTemplate restTemplate, TCGdexApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    public List<TCGdexCardBrief> searchCards(String name) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Search attempted with empty or null name");
            return Collections.emptyList();
        }
        try {
            String url = apiProperties.getBaseUrl() + "/cards?name=" + name;
            logger.info("Searching cards with URL: {}", url);

            TCGdexCardBrief[] cardsArray = restTemplate.getForObject(url, TCGdexCardBrief[].class);
            if (cardsArray == null || cardsArray.length == 0) {
                logger.info("No cards found for name: {}", name);
                return Collections.emptyList();
            }

            List<TCGdexCardBrief> cards = Arrays.asList(cardsArray);
            logger.info("Found {} cards for search term: {}", cards.size(), name);
            return cards;

        } catch (RestClientException e) {
            logger.error("Error occurred while searching for cards with name '{}': {}", name, e.getMessage());
            throw e;
        }
    }

    public TCGdexCard getCard(String cardId) {
        if (cardId == null || cardId.trim().isEmpty()) {
            logger.warn("Search attempted with empty or null name");
            return null;
        }
        try {
            String url = apiProperties.getBaseUrl() + "/cards/" + cardId.trim();
            logger.info("Fetching card with URL: {}", url);

            TCGdexCard card = restTemplate.getForObject(url, TCGdexCard.class);
            if (card == null) {
                logger.info("No card found for ID: {}", cardId);
                return null;
            }

            logger.info("Found card: {} for ID: {}", card.getName(), cardId);
            return card;

        } 
        
        catch (RestClientException e) {
            logger.error("Error occurred while fetching card with ID '{}': {}", cardId, e.getMessage());

            Throwable cause = e.getMostSpecificCause();
            if (cause instanceof com.fasterxml.jackson.databind.JsonMappingException jme) {
                logger.error("Deserialization error details: {} at path: {}", jme.getOriginalMessage(), jme.getPathReference());
            }
            
            // Check if it's a deserialization error
            if (e.getMessage().contains("extracting response")) {
                logger.warn("Deserialization failed for card ID '{}'. The API response structure may not match our DTO.", cardId);
                // You could try to fetch as a generic Map or JsonNode to see the actual structure
            }
            
            throw e;
        }
    }

}