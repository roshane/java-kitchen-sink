package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.net.URL;


@Disabled
class CsvControllerTest {

    private WebTestClient webTestClient;
    private final JdbcTemplate jdbcTemplate = null;

    @BeforeEach
    public void beforeEach() {
        webTestClient = WebTestClient
                .bindToController(new CsvController(jdbcTemplate)).build();
    }

    @Test
    void test_csvUploadShouldSuccess() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(loadClasspathCsv("Test.csv")));
        String expectedResponseContent = "lastLine processed: [2017-01,ANG MO KIO,3 ROOM,571,ANG MO KIO AVE 3,01 TO 03,67,New Generation,1979,61 years 04 months,285000]";
        webTestClient.post()
                .uri("/dataset/resale_flat_price")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                .expectBody(String.class)
                .isEqualTo(expectedResponseContent);
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