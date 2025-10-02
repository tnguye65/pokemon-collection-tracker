package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexSetBrief {
    private String id;
    private String name;
    private String logo;
    private String symbol;
    private TCGdexCardCount cardCount;

    public TCGdexSetBrief() {
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public TCGdexCardCount getCardCount() {
        return cardCount;
    }

    public void setCardCount(TCGdexCardCount cardCount) {
        this.cardCount = cardCount;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TCGdexCardCount {
        private Integer total;
        private Integer official;

        public TCGdexCardCount() {
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getOfficial() {
            return official;
        }

        public void setOfficial(Integer official) {
            this.official = official;
        }
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
