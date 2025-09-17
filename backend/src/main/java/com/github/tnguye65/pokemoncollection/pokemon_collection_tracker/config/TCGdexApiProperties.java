package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tcgdex.api")
public class TCGdexApiProperties {
    
    private String baseUrl;
    
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
}
