package com.commons.togglz.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.Feature;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.spi.FeatureProvider;
import org.togglz.mongodb.MongoStateRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({MongoDBConfig.class, CoreConfig.class})
public class AppConfig {
    @Value("${database.name}")
    private String databaseName;

    @Value("${database.table}")
    private String collectionName;
    private final CoreConfig coreConfig;

    private final MongoDBConfig mongoDBConfig;

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(MongoDBConfig mongoDBConfig, CoreConfig coreConfig) {
        this.mongoDBConfig = mongoDBConfig;
        this.coreConfig = coreConfig;
    }

    @Bean
    public FeatureProvider featureProvider() {
        coreConfig.features.forEach(fqn -> logger.info("Enabling feature for: {}", fqn));
        final EnumBasedFeatureProvider provider = new EnumBasedFeatureProvider();
        coreConfig.features
                .stream()
                .map(this::findClazzByFqn)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(provider::addFeatureEnum);
        return provider;
    }

    @Bean
    public StateRepository stateRepository() {
        return MongoStateRepository.newBuilder(mongoClient(), databaseName)
                .collection(collectionName)
                .build();
    }

    private MongoClient mongoClient() {
        final MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applicationName(mongoDBConfig.applicationName)
                .applyToClusterSettings(cs -> {
                    final List<ServerAddress> serverAddressList = mongoDBConfig.instances
                            .stream()
                            .map(hp -> {
                                final String[] hostAndPort = hp.split(":");
                                return new ServerAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
                            }).collect(Collectors.toList());
                    cs.hosts(serverAddressList);
                })
                .build();
        return MongoClients.create(clientSettings);
    }

    private Optional<Class<? extends Feature>> findClazzByFqn(String fqn) {
        try {
            return Optional.of((Class<? extends Feature>) Class.forName(fqn));
        } catch (Exception ex) {
            logger.error("Error activating feature class: " + fqn, ex);
        }
        return Optional.empty();
    }

}
