package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexPricing {

    @JsonProperty("cardmarket")
    private TCGdexCardMarketPricing cardmarket;

    @JsonProperty("tcgplayer")
    private TCGdexTcgPlayerPricing tcgplayer;
    public TCGdexPricing() {
    }

    public TCGdexCardMarketPricing getCardmarket() {
        return cardmarket;
    }

    public void setCardmarket(TCGdexCardMarketPricing cardmarket) {
        this.cardmarket = cardmarket;
    }

    public TCGdexTcgPlayerPricing getTcgplayer() {
        return tcgplayer;
    }

    public void setTcgplayer(TCGdexTcgPlayerPricing tcgplayer) {
        this.tcgplayer = tcgplayer;
    }

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

        public TCGdexTcgPlayerPricing() {
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public TCGdexPriceVariant getNormal() {
            return normal;
        }

        public void setNormal(TCGdexPriceVariant normal) {
            this.normal = normal;
        }

        public TCGdexPriceVariant getHolofoil() {
            return holofoil;
        }

        public void setHolofoil(TCGdexPriceVariant holofoil) {
            this.holofoil = holofoil;
        }

        public TCGdexPriceVariant getReverse() {
            return reverse;
        }

        public void setReverse(TCGdexPriceVariant reverse) {
            this.reverse = reverse;
        }

        public TCGdexPriceVariant getReverseHolofoil() {
            return reverseHolofoil;
        }

        public void setReverseHolofoil(TCGdexPriceVariant reverseHolofoil) {
            this.reverseHolofoil = reverseHolofoil;
        }

        public TCGdexPriceVariant getFirstEdition() {
            return firstEdition;
        }

        public void setFirstEdition(TCGdexPriceVariant firstEdition) {
            this.firstEdition = firstEdition;
        }

        public TCGdexPriceVariant getFirstEditionHolofoil() {
            return firstEditionHolofoil;
        }

        public void setFirstEditionHolofoil(TCGdexPriceVariant firstEditionHolofoil) {
            this.firstEditionHolofoil = firstEditionHolofoil;
        }

        public TCGdexPriceVariant getUnlimited() {
            return unlimited;
        }

        public void setUnlimited(TCGdexPriceVariant unlimited) {
            this.unlimited = unlimited;
        }

        public TCGdexPriceVariant getUnlimitedHolofoil() {
            return unlimitedHolofoil;
        }

        public void setUnlimitedHolofoil(TCGdexPriceVariant unlimitedHolofoil) {
            this.unlimitedHolofoil = unlimitedHolofoil;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TCGdexPriceVariant {
        private Double lowPrice;
        private Double midPrice;
        private Double highPrice;
        private Double marketPrice;
        private Double directLowPrice;

        public TCGdexPriceVariant() {
        }

        public Double getLowPrice() {
            return lowPrice;
        }

        public void setLowPrice(Double lowPrice) {
            this.lowPrice = lowPrice;
        }

        public Double getMidPrice() {
            return midPrice;
        }

        public void setMidPrice(Double midPrice) {
            this.midPrice = midPrice;
        }

        public Double getHighPrice() {
            return highPrice;
        }

        public void setHighPrice(Double highPrice) {
            this.highPrice = highPrice;
        }

        public Double getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(Double marketPrice) {
            this.marketPrice = marketPrice;
        }

        public Double getDirectLowPrice() {
            return directLowPrice;
        }

        public void setDirectLowPrice(Double directLowPrice) {
            this.directLowPrice = directLowPrice;
        }
    }

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

        public TCGdexCardMarketPricing() {
        }

        public String getUpdated() {
            return updated;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public Double getAvg() {
            return avg;
        }

        public void setAvg(Double avg) {
            this.avg = avg;
        }

        public Double getLow() {
            return low;
        }

        public void setLow(Double low) {
            this.low = low;
        }

        public Double getTrend() {
            return trend;
        }

        public void setTrend(Double trend) {
            this.trend = trend;
        }

        public Double getAvg1() {
            return avg1;
        }

        public void setAvg1(Double avg1) {
            this.avg1 = avg1;
        }

        public Double getAvg7() {
            return avg7;
        }

        public void setAvg7(Double avg7) {
            this.avg7 = avg7;
        }

        public Double getAvg30() {
            return avg30;
        }

        public void setAvg30(Double avg30) {
            this.avg30 = avg30;
        }

        public Double getAvgHolo() {
            return avgHolo;
        }

        public void setAvgHolo(Double avgHolo) {
            this.avgHolo = avgHolo;
        }

        public Double getLowHolo() {
            return lowHolo;
        }

        public void setLowHolo(Double lowHolo) {
            this.lowHolo = lowHolo;
        }

        public Double getTrendHolo() {
            return trendHolo;
        }

        public void setTrendHolo(Double trendHolo) {
            this.trendHolo = trendHolo;
        }

        public Double getAvg1Holo() {
            return avg1Holo;
        }

        public void setAvg1Holo(Double avg1Holo) {
            this.avg1Holo = avg1Holo;
        }

        public Double getAvg7Holo() {
            return avg7Holo;
        }

        public void setAvg7Holo(Double avg7Holo) {
            this.avg7Holo = avg7Holo;
        }

        public Double getAvg30Holo() {
            return avg30Holo;
        }
        
        public void setAvg30Holo(Double avg30Holo) {
            this.avg30Holo = avg30Holo;
        }
    }
}