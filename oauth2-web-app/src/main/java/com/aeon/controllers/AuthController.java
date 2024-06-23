package com.aeon.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
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
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    private static final String OAUTH_2_CLIENT = "keycloak";

    public AuthController(ClientRegistrationRepository repository,
                          OAuth2AuthorizedClientService clientService,
                          WebClient webClient,
                          OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.webClient = webClient;
        this.repository = repository;
        this.clientService = clientService;
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @GetMapping("/")
    public Map<String, Object> echo(@RegisteredOAuth2AuthorizedClient(OAUTH_2_CLIENT) OAuth2AuthorizedClient authorizedClient) {
        final Map<String,Object> wiremockResponse = webClient.get()
                .uri("https://httpbin.org/get")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        Objects.requireNonNull(authorizedClient, String.format("no authorized client found for %s", OAUTH_2_CLIENT));
        return Map.of(
                "greetings", "hello there how are you????",
                "wiremockResponse", wiremockResponse
        );
    }

    @GetMapping("/clients")
    public Map<String, String> clients() {
        final ClientRegistration registration = repository.findByRegistrationId(OAUTH_2_CLIENT);
        final OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId(registration.getRegistrationId())
                .principal("roshane@developer.com")
                .build();
        final OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientManager
                .authorize(request);
        return Map.of(
                "token", authorizedClient.getAccessToken().getTokenValue(),
                "principal", authorizedClient.getPrincipalName()
        );
    }
}
