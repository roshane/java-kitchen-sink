package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class ReactorMonoTest {

    private final Logger logger = LoggerFactory.getLogger(ReactorMonoTest.class);

    @Test
    void testA() {
        Mono.just("random-string")
                .map(Optional::of)
                .map(it -> {
                    logger.info("logging in optional: [{}]", it);
                    return it;
                }).block();

        Mono.just("mono-optional-string")
                .flatMap(it -> Mono.just(Optional.of(it)))
                .map(it -> {
                    logger.info("logging in Mono.optional: [{}]", it);
                    return it;
                }).block();
    }
}
