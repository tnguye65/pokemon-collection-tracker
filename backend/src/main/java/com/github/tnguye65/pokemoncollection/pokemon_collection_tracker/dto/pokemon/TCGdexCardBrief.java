package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.dto.pokemon;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TCGdexCardBrief {
    private String id;
    private String localId;
    private String name;
    private String image;

    public TCGdexCardBrief() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

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


