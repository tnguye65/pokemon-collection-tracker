package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexCard extends TCGdexCardBrief {
    public String category;
    public String illustrator;
    public String rarity;
    public TCGdexSetBrief set;
    public TCGdexVariants variants;

    public TCGdexBooster boosters;
    public String updated;

    @JsonProperty(value = "pricing", required = false)
    public TCGdexPricing pricing;

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

    public TCGdexCard() {
        super();
    }

    public List<Map<String, String>> getVariantsDetailed() {
        return variantsDetailed;
    }

    public void setVariantsDetailed(List<Map<String, String>> variantsDetailed) {
        this.variantsDetailed = variantsDetailed;
    }

    // Getters and setters for public fields
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIllustrator() {
        return illustrator;
    }

    public void setIllustrator(String illustrator) {
        this.illustrator = illustrator;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public TCGdexSetBrief getSet() {
        return set;
    }

    public void setSet(TCGdexSetBrief set) {
        this.set = set;
    }

    public TCGdexVariants getVariants() {
        return variants;
    }

    public void setVariants(TCGdexVariants variants) {
        this.variants = variants;
    }

    public TCGdexBooster getBoosters() {
        return boosters;
    }

    public void setBoosters(TCGdexBooster boosters) {
        this.boosters = boosters;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    // Getters and setters for private fields
    public List<Integer> getDexId() {
        return dexId;
    }

    public void setDexId(List<Integer> dexId) {
        this.dexId = dexId;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getEvolveFrom() {
        return evolveFrom;
    }

    public void setEvolveFrom(String evolveFrom) {
        this.evolveFrom = evolveFrom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public TCGdexItem getItem() {
        return item;
    }

    public void setItem(TCGdexItem item) {
        this.item = item;
    }

    public List<TCGdexAbility> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<TCGdexAbility> abilities) {
        this.abilities = abilities;
    }

    public List<TCGdexAttack> getAttacks() {
        return attacks;
    }

    public void setAttacks(List<TCGdexAttack> attacks) {
        this.attacks = attacks;
    }

    public List<TCGdexWeakness> getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(List<TCGdexWeakness> weaknesses) {
        this.weaknesses = weaknesses;
    }

    public List<TCGdexWeakness> getResistances() {
        return resistances;
    }

    public void setResistances(List<TCGdexWeakness> resistances) {
        this.resistances = resistances;
    }

    public Integer getRetreat() {
        return retreat;
    }

    public void setRetreat(Integer retreat) {
        this.retreat = retreat;
    }

    public String getRegulationMark() {
        return regulationMark;
    }

    public void setRegulationMark(String regulationMark) {
        this.regulationMark = regulationMark;
    }

    public TCGdexLegal getLegal() {
        return legal;
    }

    public void setLegal(TCGdexLegal legal) {
        this.legal = legal;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getTrainerType() {
        return trainerType;
    }

    public void setTrainerType(String trainerType) {
        this.trainerType = trainerType;
    }

    public String getEnergyType() {
        return energyType;
    }

    public void setEnergyType(String energyType) {
        this.energyType = energyType;
    }
}