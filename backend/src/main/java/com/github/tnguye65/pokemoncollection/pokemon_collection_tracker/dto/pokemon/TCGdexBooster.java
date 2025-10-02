package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexBooster {
    private String id;
    private String name;
    private String logo;
    
    @JsonProperty("artwork_front")
    private String artworkFront;
    
    @JsonProperty("artwork_back")
    private String artworkBack;

    public TCGdexBooster() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getArtworkFront() {
        return artworkFront;
    }

    public void setArtworkFront(String artworkFront) {
        this.artworkFront = artworkFront;
    }

    public String getArtworkBack() {
        return artworkBack;
    }

    public void setArtworkBack(String artworkBack) {
        this.artworkBack = artworkBack;
    }
}
