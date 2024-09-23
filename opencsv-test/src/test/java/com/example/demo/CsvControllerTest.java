package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.net.URL;


class CsvControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    public void beforeEach() {
        webTestClient = WebTestClient
                .bindToController(new DemoApplication.CsvController()).build();
    }

    @Test
    void test_csvUploadShouldSuccess() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(loadClasspathCsv("Test.csv")));

        webTestClient.post()
                .uri("/dataset")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_PLAIN_VALUE)
                .expectBody(String.class)
                .isEqualTo("done");
    }

    File loadClasspathCsv(String relativePath) {
        URL resource = CsvControllerTest.class.getClassLoader().getResource(relativePath);
        try {
            return new File(resource.toURI());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}