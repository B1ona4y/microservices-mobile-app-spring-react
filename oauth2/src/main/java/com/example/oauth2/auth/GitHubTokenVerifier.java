package com.example.oauth2.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class GitHubTokenVerifier {
    
    private final RestClient apiClient = RestClient.create("https://api.github.com");
    private final RestClient oauthClient = RestClient.create("https://github.com/login/oauth");

    private final String clientId;
    private final String clientSecret;

    public GitHubTokenVerifier(
            @Value("${app.github.client-id}") String clientId,
            @Value("${app.github.client-secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public GitHubUser verify(String code) {
        String accessToken = exchangeCodeForAccessToken(code);
        GitHubUserResponse response = apiClient.get()
                                    .uri("/user")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                    .retrieve()
                                    .body(GitHubUserResponse.class);
        if (response == null){
            throw new IllegalArgumentException("Invalid GitHub token");
        }
        String email = response.email() != null ? response.email() : fetchPrimaryEmail(accessToken);
		String name = response.name() != null ? response.name() : response.login();
		return new GitHubUser(email, name);
    }
    
    @SuppressWarnings("null")
    private String fetchPrimaryEmail(String accessToken) {
        List<GitHubEmail> emails = apiClient.get()
                .uri("/user/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<GitHubEmail>>() {});
        if (emails == null) {
            return null;
        }
        return emails.stream()
                .filter(GitHubEmail::primary)
                .filter(GitHubEmail::verified)
                .map(GitHubEmail::email)
                .findFirst()
                .orElse(null);
    }

    private String exchangeCodeForAccessToken(String code) {
        AccessTokenResponse response = oauthClient.post()
                .uri("/access_token")
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new AccessTokenRequest(clientId, clientSecret, code))
                .retrieve()
                .body(AccessTokenResponse.class);
        if (response == null || response.accessToken() == null) {
            throw new IllegalArgumentException("Failed to exchange GitHub code for an access token");
        }
        return response.accessToken();
    }

    private record AccessTokenRequest(
        @JsonProperty("client_id") String clientId,
        @JsonProperty("client_secret") String clientSecret,
        String code) {
    }

    private record AccessTokenResponse(@JsonProperty("access_token") String accessToken) {
    }

    private record GitHubUserResponse(String login, String name, String email) {
	}

	public record GitHubUser(String email, String name) {
	}

    private record GitHubEmail(String email, boolean primary, boolean verified) {
    }
}
