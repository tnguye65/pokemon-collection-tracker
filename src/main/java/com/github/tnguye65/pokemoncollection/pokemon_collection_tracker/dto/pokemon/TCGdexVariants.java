package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexVariants {
    private Boolean normal;
    private Boolean reverse;
    private Boolean holo;
    private Boolean firstEdition;
    private Boolean wPromo;
}