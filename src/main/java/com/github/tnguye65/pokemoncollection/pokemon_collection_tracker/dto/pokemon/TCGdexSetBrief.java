package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexSetBrief {
    private String id;
    private String name;
    private String logo;
    private String symbol;
    private TCGdexCardCount cardCount;
    
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TCGdexCardCount {
        private Integer total;
        private Integer official;
    }
    
    /**
     * Get high quality logo URL
     */
    public String getHighQualityLogoUrl() {
        if (logo == null) return null;
        return logo + "/high.png";
    }
    
    /**
     * Get high quality symbol URL
     */
    public String getHighQualitySymbolUrl() {
        if (symbol == null) return null;
        return symbol + "/high.png";
    }

}
