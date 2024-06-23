package com.aeon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class OAuth2Config {


    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().permitAll()
                );
        return httpSecurity.build();
    }

    @Bean
    WebClient webClient(OAuth2AuthorizedClientManager clientManager) {
        var filter = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientManager);
        return WebClient.builder()
                .apply(filter.oauth2Configuration())
                .build();
    }


    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials(builder -> builder.accessTokenResponseClient(tokenResponseClient()))
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    DefaultClientCredentialsTokenResponseClient tokenResponseClient() {
        var tokenResponseClient = new DefaultClientCredentialsTokenResponseClient();
        OAuth2ClientCredentialsGrantRequestEntityConverter converter =
                new OAuth2ClientCredentialsGrantRequestEntityConverter();
        converter.addHeadersConverter(headersConverter());
        tokenResponseClient.setRequestEntityConverter(converter);
        return tokenResponseClient;
    }

    private Converter<OAuth2ClientCredentialsGrantRequest, HttpHeaders> headersConverter() {
        return (request) -> {
            final HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.USER_AGENT, request.getClientRegistration().getClientId());
            return headers;
        };
    }
}
