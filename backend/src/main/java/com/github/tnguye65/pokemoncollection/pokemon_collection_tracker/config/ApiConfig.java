package com.github.tnguye65.pokemoncollection.pokemon_collection_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        
        // Use built-in SimpleClientHttpRequestFactory instead
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);    // 5 seconds
        factory.setReadTimeout(10000);      // 10 seconds
        template.setRequestFactory(factory);
        
        return template;
    }
}
