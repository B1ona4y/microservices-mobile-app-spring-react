package com.example.oauth2.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubTokenVerifier {
    
    private final RestClient restClient = RestClient.create("https://api.github.com");

    public GitHubUser verify(String accessToken) {
        GitHubUserResponse response = restClient.get()
                                    .uri("/user")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                    .retrieve()
                                    .body(GitHubUserResponse.class);
        if (response == null){
            throw new IllegalArgumentException("Invalid GitHub token");
        }
        String email = response.email() != null ? response.email() : response.login() + "@users.noreply.github.com";
		String name = response.name() != null ? response.name() : response.login();
		return new GitHubUser(email, name);
    }

    private record GitHubUserResponse(String login, String name, String email) {
	}

	public record GitHubUser(String email, String name) {
	}

}
