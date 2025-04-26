package com.aeon.commandline.commands.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class JsonPlaceholderService {

    private static final RestClient client = RestClient.create();

    public List<Map<String, String>> todosJson() {
        return client.get()
                .uri("https://jsonplaceholder.typicode.com/todos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, String>>>() {
                });
    }
}
