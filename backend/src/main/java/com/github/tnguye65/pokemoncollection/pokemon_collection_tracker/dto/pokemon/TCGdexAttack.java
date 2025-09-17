package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexAttack {
    private String name;
    private List<String> cost;
    private String effect;
    private Integer damage;
}
