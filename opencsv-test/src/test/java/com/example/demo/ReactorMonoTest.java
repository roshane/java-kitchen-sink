package com.example.demo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

@Disabled
public class ReactorMonoTest {

    private final Logger logger = LoggerFactory.getLogger(ReactorMonoTest.class);
    private final RestClient client = RestClient
            .builder(
                    new RestTemplateBuilder()
                            .setReadTimeout(Duration.ofMillis(6000))
                            .build()
            )
            .build();


    private String readClasspathResource(String fileName) {
        return Util.fromThrowable(() -> {
            InputStream inputStream = ReactorMonoTest.class.getClassLoader().getResource(fileName).openStream();
            String result = new String(inputStream.readAllBytes());
            inputStream.close();
            return result;
        });
    }


    @Test
    void webRequester() {
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add(
                "file",
                new FileSystemResource(
                        Path.of(
                                "/home/roshane/IdeaProjects/java-kitchen-sink/opencsv-test/src/main/resources/Test.csv"
                        )
                )
        );
        Map<String, Object> response = client
                .post()
                .uri(URI.create("http://localhost:8686/dataset/resale_flat_price"))
                .body(formData)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .getBody();
        logger.info("response: [{}]", response);

    }
}
