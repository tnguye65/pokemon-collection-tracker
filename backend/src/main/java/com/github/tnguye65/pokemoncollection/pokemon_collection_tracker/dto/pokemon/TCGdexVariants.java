package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexVariants {
    private Boolean normal;
    private Boolean reverse;
    private Boolean holo;
    private Boolean firstEdition;
    private Boolean wPromo;

    public TCGdexVariants() {
    }

    public Boolean getNormal() {
        return normal;
    }

    public void setNormal(Boolean normal) {
        this.normal = normal;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public Boolean getHolo() {
        return holo;
    }

    public void setHolo(Boolean holo) {
        this.holo = holo;
    }

    public Boolean getFirstEdition() {
        return firstEdition;
    }

    public void setFirstEdition(Boolean firstEdition) {
        this.firstEdition = firstEdition;
    }

    public Boolean getwPromo() {
        return wPromo;
    }

    public void setwPromo(Boolean wPromo) {
        this.wPromo = wPromo;
    }
}