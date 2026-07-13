package com.example.oauth2.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class GoogleIdTokenVerifier {
    private NimbusJwtDecoder decoder;

    public GoogleIdTokenVerifier(@Value("${app.google.client-id}") String clientId) {
        this.decoder = NimbusJwtDecoder
            .withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            .build();

		OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("https://accounts.google.com");
        OAuth2TokenValidator<Jwt> withAudience = jwt -> {
            if (jwt.getAudience() != null && jwt.getAudience().contains(clientId)){
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", "Unexpected audience", null));
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(List.of(withIssuer, withAudience)));
    }

    public GoogleUser verify(String idToken) {
        Jwt jwt = decoder.decode(idToken);
        return new GoogleUser(jwt.getClaimAsString("email"), jwt.getClaimAsString("name"));
    }

    public record GoogleUser(String email, String name) {
    }
}
