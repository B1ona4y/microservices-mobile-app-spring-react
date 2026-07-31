package com.example.oauth2.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.oauth2.refresh.InvalidRefreshTokenException;
import com.example.oauth2.refresh.RefreshToken;
import com.example.oauth2.refresh.RefreshTokenService;
import com.example.oauth2.refresh.TokenReuseDetectedException;
import com.example.oauth2.user.User;
import com.example.oauth2.user.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
	private final JwtService jwtService;
	private final GoogleIdTokenVerifier googleVerifier;
	private final GitHubTokenVerifier gitHubVerifier;
	private final RefreshTokenService refreshTokenService;

    public AuthController(GitHubTokenVerifier gitHubVerifier,
                    GoogleIdTokenVerifier googleVerifier,
                    JwtService jwtService,
                    UserService userService,
                    RefreshTokenService refreshTokenService) {
        this.gitHubVerifier = gitHubVerifier;
        this.googleVerifier = googleVerifier;
        this.jwtService = jwtService;
        this.userService = userService;
		this.refreshTokenService = refreshTokenService;
    }

    public record RegisterRequest(String email, String password, String name) { }

	public record LoginRequest(String email, String password) { }

	public record ProviderTokenRequest(String token) { }

	public record AuthResponse(String accessToken, String refreshToken) { }

	public record GithubCodeRequest(String code) { }

	public record RefreshRequest(String refreshToken) { }

    @PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		return userService.register(request.email(), request.name(), request.password())
				.map(user -> ResponseEntity.ok(new AuthResponse(
					jwtService.issueToken(String.valueOf(user.getId()), user.getName()),
					refreshTokenService.issueNewFamily(user.getId()))))
				.orElseGet(() -> ResponseEntity.status(409).build());
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		return userService.findByEmail(request.email())
				.filter(user -> userService.checkPassword(user, request.password()))
				.map(user -> ResponseEntity.ok(new AuthResponse(
					jwtService.issueToken(String.valueOf(user.getId()), user.getName()),
					refreshTokenService.issueNewFamily(user.getId()))))
				.orElseGet(() -> ResponseEntity.status(401).build());
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request){
		try {
			RefreshToken old = refreshTokenService.rotate(request.refreshToken());
			User user = userService.findById(old.getUserId())
				.orElseThrow(() -> new InvalidRefreshTokenException("user not found"));

			String newAccess = jwtService.issueToken(String.valueOf(user.getId()), user.getName());
			String newRefresh = refreshTokenService.issueToken(old.getUserId(), old.getFamilyId());
			return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh));
		} catch (TokenReuseDetectedException | InvalidRefreshTokenException e) {
			return ResponseEntity.status(401).build();
		}
	}

	@PostMapping("/google")
	public ResponseEntity<AuthResponse> google(@RequestBody ProviderTokenRequest request) {
		try {
			GoogleIdTokenVerifier.GoogleUser googleUser = googleVerifier.verify(request.token());
			User user = userService.findOrCreateOAuthUser(googleUser.email(), googleUser.name());
			return ResponseEntity.ok(new AuthResponse(
				jwtService.issueToken(String.valueOf(user.getId()), user.getName()),
				refreshTokenService.issueNewFamily(user.getId())));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).build();
		}
	}

	@PostMapping("/github")
	public ResponseEntity<AuthResponse> github(@RequestBody GithubCodeRequest request) {
		try {
			GitHubTokenVerifier.GitHubUser gitHubUser = gitHubVerifier.verify(request.code());
			User user = userService.findOrCreateOAuthUser(gitHubUser.email(), gitHubUser.name());
			return ResponseEntity.ok(new AuthResponse(
				jwtService.issueToken(String.valueOf(user.getId()), user.getName()),
				refreshTokenService.issueNewFamily(user.getId())));
		} catch (RuntimeException e) {
			return ResponseEntity.status(401).build();
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody RefreshRequest request){
		refreshTokenService.revoke(request.refreshToken());
    	return ResponseEntity.noContent().build();
	}

}
