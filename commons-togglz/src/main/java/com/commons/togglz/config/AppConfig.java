package com.commons.togglz.config;

import com.commons.togglz.feature.IFeatureProvider;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
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
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({MongoDBConfig.class})
public class AppConfig {
    @Value("${database.name}")
    private String databaseName;

    @Value("${database.table}")
    private String collectionName;
    private final MongoDBConfig mongoDBConfig;

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public AppConfig(MongoDBConfig mongoDBConfig) {
        this.mongoDBConfig = mongoDBConfig;
    }

    @Bean
    public FeatureProvider featureProvider() {
        final EnumBasedFeatureProvider provider = new EnumBasedFeatureProvider();
        loadFeatures().forEach(f -> {
            logger.info("Enabling feature: {}", f.getName());
            provider.addFeatureEnum(f);
        });
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
                .applyToClusterSettings(cs -> cs.applyConnectionString(new ConnectionString(mongoDBConfig.connectionString)))
                .build();
        return MongoClients.create(clientSettings);
    }

    private List<Class<? extends Feature>> loadFeatures() {
        ServiceLoader<IFeatureProvider> featureProviders = ServiceLoader.load(IFeatureProvider.class);
        return featureProviders
                .stream()
                .map(ServiceLoader.Provider::get)
                .flatMap(fp -> fp.features().stream())
                .collect(Collectors.toList());
    }

}
