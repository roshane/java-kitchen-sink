package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


class JsonTest {

    private static final ObjectMapper mapper = new Jackson2ObjectMapperBuilder()
            .modules(new Jdk8Module())
            .build();

    @Test
    void testDeserializeRootA() throws Exception {
        String jsonString = """
                {
                    "name": "type-a",
                    "id": 1
                }
                """;
        Root result = mapper.readValue(jsonString, Root.class);
        assertInstanceOf(TypeA.class, result);
        TypeA a = (TypeA) result;
        assertEquals("type-a", a.getName());
        assertEquals(1, a.getId());
    }

    @Test
    void testDeserializeRootB() throws Exception {
        String jsonString = """
                {
                    "name": "type-b",
                    "id": 2,
                    "entity": {
                        "name" : "simple entity"
                    }
                }
                """;
        Root result = mapper.readValue(jsonString, Root.class);
        assertInstanceOf(TypeB.class, result);
        TypeB b = (TypeB) result;
        assertEquals("type-b", b.getName());
        assertEquals(2, b.getId());
        assertEquals("simple entity", b.getEntity().getName());
    }
}
