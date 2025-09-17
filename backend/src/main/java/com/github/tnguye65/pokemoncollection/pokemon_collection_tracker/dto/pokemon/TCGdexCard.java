package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexCard extends TCGdexCardBrief {
    public String category;
    public String illustrator;
    public String rarity;
    public TCGdexSetBrief set;
    public TCGdexVariants variants;
    public TCGdexBooster boosters;
    public String updated;

    public TCGdexPricing pricing; // PRICING WAS THE ISSUE WITH DESEARIALIZATION

    @JsonProperty("variants_detailed")
    private List<Map<String, String>> variantsDetailed;

    // Pokemon-specific fields
    private List<Integer> dexId;
    private Integer hp;
    private List<String> types;
    private String evolveFrom;
    private String description;
    private String level;
    private String stage;
    private String suffix;
    private TCGdexItem item;

    private List<TCGdexAbility> abilities = new ArrayList<>();
    private List<TCGdexAttack> attacks = new ArrayList<>();
    private List<TCGdexWeakness> weaknesses = new ArrayList<>();
    private List<TCGdexWeakness> resistances = new ArrayList<>();

    private Integer retreat;
    private String regulationMark;
    private TCGdexLegal legal; // This should always be present according to Kotlin SDK

    // Trainer-specific fields
    private String effect;
    private String trainerType;

    // Energy-specific fields
    private String energyType;

}