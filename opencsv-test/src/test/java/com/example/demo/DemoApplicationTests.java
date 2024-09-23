package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

    private final MockMvc mvc;

    DemoApplicationTests(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void test_homeShouldReturnCorrectResponse() throws Exception {
        String expectedResponseBody = """
                {"message":"hello work mint linux rocks"}
                """.stripIndent();
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    void test_entityTypeA() throws Exception {
        String expectedBody = TypeA.class.getCanonicalName();
        mvc.perform(
                        post("/entity")
                                .content("""
                                        {
                                            "name": "A",
                                            "specificTypeA": "AA"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @ParameterizedTest
    @MethodSource("entityTestParamProvider")
    void test_entity(String requestJson,
                     String expectedResponse) throws Exception {
        mvc.perform(
                        post("/entity")
                                .content(requestJson)
                                .contentType(MediaType.APPLICATION_JSON)

                )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }

    static Stream<Arguments> entityTestParamProvider() {
        return Stream.of(
                Arguments.arguments(
                        """
                                {
                                    "name": "A",
                                    "id": 1
                                }""".stripIndent(),
                        TypeA.class.getCanonicalName()
                ),
                Arguments.arguments(
                        """
                                {
                                    "name": "B",
                                    "id": 2
                                }""".stripIndent(),
                        TypeB.class.getCanonicalName()
                )
        );
    }

}
