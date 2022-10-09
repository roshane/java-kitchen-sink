package com.commons.togglz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@ConstructorBinding
@ConfigurationProperties("mongo")
public class MongoDBConfig {

    public final String applicationName;
    public final List<String> instances;
    public final String collectionName;

    public MongoDBConfig(String applicationName,
                         String collectionName,
                         List<String> instances) {
        this.instances = instances;
        this.collectionName = collectionName;
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "MongoDBConfig{" +
                "applicationName='" + applicationName + '\'' +
                ", instances=" + instances +
                ", collectionName='" + collectionName + '\'' +
                '}';
    }
}
