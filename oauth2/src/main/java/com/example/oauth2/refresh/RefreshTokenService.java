package com.example.oauth2.refresh;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final SecureRandom random = new SecureRandom();
    private final long refreshTtlSeconds; // 604800 = 7 days

    public RefreshTokenService(
            RefreshTokenRepository repo,
            @Value("${app.refresh.expiry-seconds:604800}") long refreshTtlSeconds) {
        this.repo = repo;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public String issueToken(UUID userId, UUID familyId) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken entity = RefreshToken.builder()
            .tokenHash(sha256(rawToken))
            .familyId(familyId)
            .userId(userId)
            .expiresAt(Instant.now().plusSeconds(refreshTtlSeconds))
            .createdAt(Instant.now())
            .build();

        repo.save(entity);
        return rawToken;
        
	}

    public String issueNewFamily(UUID userId) {
        return issueToken(userId, UUID.randomUUID());
    }

    public RefreshToken rotate(String presentedRawToken) {
        RefreshToken token = repo.findByTokenHash(sha256(presentedRawToken))
            .orElseThrow(() -> new InvalidRefreshTokenException("not found"));

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())){
            throw new InvalidRefreshTokenException("revoked or expired");
        }
        
        if (token.isUsed()) {
            revokeFamily(token.getFamilyId());
            throw new TokenReuseDetectedException("reuse detected");
        }

        token.setUsed(true);
        repo.save(token);
        return token;
    }

    public void revoke(String presentedRawToken) {
        repo.findByTokenHash(sha256(presentedRawToken))
            .ifPresent(token -> revokeFamily(token.getFamilyId()));
    }
    
    public void revokeFamily(UUID familyId) {
        List<RefreshToken> family = repo.findByFamilyId(familyId);
        family.forEach(t -> t.setRevoked(true));
        repo.saveAll(family);
    }

    private String sha256(String s) {
        try {
            byte[] h = MessageDigest.getInstance("SHA-256")
                .digest(s.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(h);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
