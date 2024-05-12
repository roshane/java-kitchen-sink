package com.aeon.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Objects;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RequestMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.ALL_VALUE
)
@RestController
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final ClientRegistrationRepository repository;
    private final OAuth2AuthorizedClientService clientService;
    private final WebClient webClient;

    public AuthController(ClientRegistrationRepository repository,
                          OAuth2AuthorizedClientService clientService, WebClient webClient) {
        this.webClient = webClient;
        this.repository = repository;
        this.clientService = clientService;
    }

    @GetMapping("/")
    public Map<String, Object> echo(@RegisteredOAuth2AuthorizedClient("local") OAuth2AuthorizedClient authorizedClient) {
        final String wiremockResponse = webClient.get()
                .uri("http://localhost:9090/echo")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        Objects.requireNonNull(authorizedClient, "no google");
        return Map.of(
                "greetings", "hello there how are you????",
                "tokenResponse", authorizedClient.getAccessToken(),
                "wiremockResponse", wiremockResponse
        );
    }

    @GetMapping("/clients")
    public Map<String, String> clients() {
        final ClientRegistration client = repository.findByRegistrationId("google");
        final OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient("google", "no-name");
        logger.info("authorized client: {}", authorizedClient.getAccessToken());
        return Map.of(
                "client_id", client.getClientId(),
                "clientName", client.getClientName()
        );

    }
}
