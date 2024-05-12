package com.aeon.mics;

import io.netty.handler.ssl.PemPrivateKey;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class MicsTests {

    private final Logger logger = LoggerFactory.getLogger(MicsTests.class);

    @Test
    void testPemReader() throws IOException {
        try (InputStream stream = MicsTests.class.getClassLoader().getResourceAsStream("private_key.pem")) {
            final PemPrivateKey privateKey = PemPrivateKey.valueOf(Objects.requireNonNull(stream).readAllBytes());
            logger.info("Format: {}", privateKey.getFormat());
        }
    }
}
