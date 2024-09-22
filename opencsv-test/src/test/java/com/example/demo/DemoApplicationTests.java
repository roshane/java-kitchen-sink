package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class DemoApplicationTests {

//    @Test
    void test_homeShouldReturnCorrectResponse(@Autowired WebTestClient client) {
        client.get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody()
                .json("""
                        {"message":"hello work mint linux rocks"}
                        """);
    }

}
