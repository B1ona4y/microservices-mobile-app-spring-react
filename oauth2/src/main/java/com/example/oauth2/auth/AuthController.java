package com.example.oauth2.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauth2.user.User;
import com.example.oauth2.user.UserStore;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserStore userStore;
	private final JwtService jwtService;
	private final GoogleIdTokenVerifier googleVerifier;
	private final GitHubTokenVerifier gitHubVerifier;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(GitHubTokenVerifier gitHubVerifier,
                    GoogleIdTokenVerifier googleVerifier,
                    JwtService jwtService,
                    UserStore userStore) {
        this.gitHubVerifier = gitHubVerifier;
        this.googleVerifier = googleVerifier;
        this.jwtService = jwtService;
        this.userStore = userStore;
    }

    public record RegisterRequest(String email, String password, String name) {
	}

	public record LoginRequest(String email, String password) {
	}

	public record ProviderTokenRequest(String token) {
	}

	public record AuthResponse(String token) {
	}

    @PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		if (userStore.findByEmail(request.email()).isPresent()) {
			return ResponseEntity.status(409).build();
		}
		User user = new User(request.email(), request.name(), passwordEncoder.encode(request.password()));
		userStore.save(user);
		return ResponseEntity.ok(new AuthResponse(jwtService.issueToken(user.email(), user.name())));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		return userStore.findByEmail(request.email())
				.filter(user -> user.passwordHash() != null && passwordEncoder.matches(request.password(), user.passwordHash()))
				.map(user -> ResponseEntity.ok(new AuthResponse(jwtService.issueToken(user.email(), user.name()))))
				.orElseGet(() -> ResponseEntity.status(401).build());
	}

	@PostMapping("/google")
	public ResponseEntity<AuthResponse> google(@RequestBody ProviderTokenRequest request) {
		try {
			GoogleIdTokenVerifier.GoogleUser googleUser = googleVerifier.verify(request.token());
			User user = userStore.findByEmail(googleUser.email())
					.orElseGet(() -> userStore.save(new User(googleUser.email(), googleUser.name(), null)));
			return ResponseEntity.ok(new AuthResponse(jwtService.issueToken(user.email(), user.name())));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).build();
		}
	}

	@PostMapping("/github")
	public ResponseEntity<AuthResponse> github(@RequestBody ProviderTokenRequest request) {
		try {
			GitHubTokenVerifier.GitHubUser gitHubUser = gitHubVerifier.verify(request.token());
			User user = userStore.findByEmail(gitHubUser.email())
					.orElseGet(() -> userStore.save(new User(gitHubUser.email(), gitHubUser.name(), null)));
			return ResponseEntity.ok(new AuthResponse(jwtService.issueToken(user.email(), user.name())));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).build();
		}
	}

}
