package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexCardBrief {
    private String id;
    private String localId;
    private String name;
    private String image;

    /**
     * Get high quality image URL for display
     * Use this method when you need the full-size card image
     */
    public String getHighQualityImageUrl() {
        if (image == null) return null;
        return image + "/high.png";
    }
    
    /**
     * Get thumbnail image URL for lists/previews
     * Use this method for card lists or small previews
     */
    public String getThumbnailImageUrl() {
        if (image == null) return null;
        return image + "/low.webp";
    }
    
    /**
     * Get custom image URL with specified quality and format
     * Quality can be "low" or "high"
     * Format can be "png", "webp", or "jpg"
     */
    public String getCustomImageUrl(String quality, String format) {
        if (image == null) return null;
        return image + "/" + quality + "." + format;
    }
}


