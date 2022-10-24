package com.commons.togglz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("mongo")
public class MongoDBConfig {

    public final String applicationName;
    public final String connectionString;
    public final String collectionName;

    public MongoDBConfig(String applicationName,
                         String collectionName,
                         String connectionString) {
        this.connectionString = connectionString;
        this.collectionName = collectionName;
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "MongoDBConfig{" +
                "applicationName='" + applicationName + '\'' +
                ", connectionString=" + connectionString +
                ", collectionName='" + collectionName + '\'' +
                '}';
    }
}
