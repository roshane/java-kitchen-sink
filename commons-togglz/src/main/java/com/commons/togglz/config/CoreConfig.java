package com.commons.togglz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@ConstructorBinding
@ConfigurationProperties("core")
public class CoreConfig {

    public final List<String> features;

    public CoreConfig(List<String> features) {
        this.features = features;
    }
}
