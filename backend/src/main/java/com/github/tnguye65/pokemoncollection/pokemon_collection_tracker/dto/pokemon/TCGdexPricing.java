package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexPricing {
    private TCGdexCardMarketPricing cardmarket;
    private TCGdexTcgPlayerPricing tcgplayer;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TCGdexTcgPlayerPricing {
        private String updated; // Changed from Long to String to match "2025-08-05T20:07:54.000Z"
        private String unit; // Changed from Integer to String to match "USD"
        private TCGdexPriceVariant normal;
        private TCGdexPriceVariant holofoil;
        private TCGdexPriceVariant reverse; // This matches the actual JSON response

        @JsonProperty("reverse-holofoil")
        private TCGdexPriceVariant reverseHolofoil;

        @JsonProperty("1st-edition")
        private TCGdexPriceVariant firstEdition;

        @JsonProperty("1st-edition-holofoil")
        private TCGdexPriceVariant firstEditionHolofoil;

        private TCGdexPriceVariant unlimited;

        @JsonProperty("unlimited-holofoil")
        private TCGdexPriceVariant unlimitedHolofoil;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TCGdexPriceVariant {
        private Double lowPrice;
        private Double midPrice;
        private Double highPrice;
        private Double marketPrice;
        private Double directLowPrice;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TCGdexCardMarketPricing {
        private String updated; // Changed from Long to String to match "2025-08-05T00:42:15.000Z"
        private String unit; // This was already String, which is correct
        private Double avg;
        private Double low;
        private Double trend;
        private Double avg1;
        private Double avg7;
        private Double avg30;

        @JsonProperty("avg-holo")
        private Double avgHolo;

        @JsonProperty("low-holo")
        private Double lowHolo;

        @JsonProperty("trend-holo")
        private Double trendHolo;

        @JsonProperty("avg1-holo")
        private Double avg1Holo;

        @JsonProperty("avg7-holo")
        private Double avg7Holo;

        @JsonProperty("avg30-holo")
        private Double avg30Holo;
    }
}