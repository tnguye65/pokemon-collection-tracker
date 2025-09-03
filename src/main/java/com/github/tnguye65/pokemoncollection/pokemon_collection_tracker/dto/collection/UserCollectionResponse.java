package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon.TCGdexCard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectionResponse {
    private Long id;
    private String cardId;
    private String variant;
    private Integer quantity;
    private String condition;
    private String notes;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime addedDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedDate;
    
    private TCGdexCard cardDetails; // Live card data from TCGdex API
    
    /**
     * Constructor without card details (for when API call fails)
     */
    public UserCollectionResponse(Long id, String cardId, String variant, Integer quantity, 
                                String condition, String notes, LocalDateTime addedDate, 
                                LocalDateTime updatedDate) {
        this.id = id;
        this.cardId = cardId;
        this.variant = variant;
        this.quantity = quantity;
        this.condition = condition;
        this.notes = notes;
        this.addedDate = addedDate;
        this.updatedDate = updatedDate;
        this.cardDetails = null;
    }
}