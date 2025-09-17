package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.collection;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCollectionItemRequest {
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String condition;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
