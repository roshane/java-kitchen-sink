package com.aeon.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;

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
    WebClient webClient(ClientRegistrationRepository clientRegistrationRepository,
                        OAuth2AuthorizedClientRepository authorizedClientRepository) {
        var manager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                authorizedClientRepository);
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(manager);
        final WebClient client = WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .build();
        return client;
    }

//    @Bean
//    DefaultClientCredentialsTokenResponseClient responseClient() {
//        Function<ClientRegistration, JWK> jwkResolver = (clientRegistration) -> {
//            if (clientRegistration.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.CLIENT_SECRET_JWT)) {
//                byte[] secretBytes256 = new byte[256];
//                final byte[] secretBytes = clientRegistration.getClientSecret().getBytes(StandardCharsets.UTF_8);
//                System.arraycopy(secretBytes, 0, secretBytes256, 0, secretBytes.length);
//                SecretKeySpec secretKey = new SecretKeySpec(
//                        secretBytes256,
//                        "HmacSHA256");
//                return new OctetSequenceKey.Builder(secretKey)
//                        .keyID(UUID.randomUUID().toString())
//                        .build();
//            }
//            return null;
//        };
//        OAuth2ClientCredentialsGrantRequestEntityConverter requestEntityConverter =
//                new OAuth2ClientCredentialsGrantRequestEntityConverter();
//        requestEntityConverter.addParametersConverter(
//                new NimbusJwtClientAuthenticationParametersConverter<>(jwkResolver));
//
//        DefaultClientCredentialsTokenResponseClient tokenResponseClient =
//                new DefaultClientCredentialsTokenResponseClient();
//        tokenResponseClient.setRequestEntityConverter(requestEntityConverter);
//        return tokenResponseClient;
//    }
//
//    @Bean
//    ClientRegistrationRepository clientRegistrationRepository() {
//        return new InMemoryClientRegistrationRepository(this.googleClientRegistration());
//    }
//
//    private ClientRegistration googleClientRegistration() {
//        return ClientRegistration.withRegistrationId("google")
//                .clientId("1078677407703-gcee2fcd1toj2vd89ao4ci8q5qf9m160.apps.googleusercontent.com")
//                .clientSecret("xqqJxsBsn4RTQOx08c6szcCg")
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                .redirectUri("http://localhost:8080/authorized/google")
//                .scope("openid", "profile", "email", "address", "phone")
//                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
//                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .userNameAttributeName(IdTokenClaimNames.SUB)
//                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
//                .clientName("google")
//                .build();
//    }
//
//    @Bean
//    public OAuth2AuthorizedClientManager authorizedClientManager(
//            ClientRegistrationRepository clientRegistrationRepository,
//            OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//        OAuth2AuthorizedClientProvider authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .authorizationCode()
//                        .refreshToken()
//                        .clientCredentials()
//                        .build();
//
//        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
//                new DefaultOAuth2AuthorizedClientManager(
//                        clientRegistrationRepository,
//                        authorizedClientRepository
//                );
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//        return authorizedClientManager;
//    }
}
