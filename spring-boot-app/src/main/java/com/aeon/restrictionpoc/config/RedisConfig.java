package com.aeon.restrictionpoc.config;

import com.aeon.restrictionpoc.domain.User;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    public static final String USER_LOCAL_MAP = "USER_LOCAL_MAP";
    public static final String USER_MAP = "USER_MAP";

    @Bean
    public RedissonClient redisson(Codec codec) {
        Config config = new Config();
        config.setCodec(codec);
        config.useSingleServer()
                .setClientName("spring-boot-app")
                .setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }

    @Bean
    public Codec codec() {
        final JsonJacksonCodec codec = JsonJacksonCodec.INSTANCE;
        codec.getObjectMapper().registerModule(JsonConfig.jacksonModule);
        return codec;
    }

    @Bean(USER_LOCAL_MAP)
    public RLocalCachedMap<String, User> userRLocalCachedMap(RedissonClient redissonClient) {
        final LocalCachedMapOptions<String, User> defaults = LocalCachedMapOptions
                .defaults();
        return redissonClient.getLocalCachedMap(USER_LOCAL_MAP, defaults);
    }

    @Bean(USER_MAP)
    public RMap<String, User> userRMap(RedissonClient redissonClient) {
        return redissonClient.getMap(USER_MAP);
    }
}
