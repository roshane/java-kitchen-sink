package com.aeon.restrictionpoc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class WebConfig {
    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void customizeObjectMapper() {
        objectMapper.registerModule(JsonConfig.jacksonModule);
    }

}
