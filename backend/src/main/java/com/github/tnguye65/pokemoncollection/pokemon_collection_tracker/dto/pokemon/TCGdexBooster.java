package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexBooster {
    private String id;
    private String name;
    private String logo;
    
    @JsonProperty("artwork_front")
    private String artworkFront;
    
    @JsonProperty("artwork_back")
    private String artworkBack;
}
