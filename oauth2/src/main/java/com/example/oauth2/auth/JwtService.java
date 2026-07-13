package com.example.oauth2.auth;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {
    private final MACSigner signer;
    private final String issuer;
    private final long expirySeconds;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
		@Value("${app.jwt.issuer:oauth2-demo}") String issuer,
		@Value("${app.jwt.expiry-seconds:3600}") long expirySeconds) throws JOSEException {
        this.signer = new MACSigner(secret.getBytes());
        this.issuer = issuer;
        this.expirySeconds = expirySeconds;
    }

    public String issueToken(String subjectEmail, String name) {
        try {
            Instant now = Instant.now();
            JWTClaimsSet claim = new JWTClaimsSet.Builder()
                .subject(subjectEmail)
                .issuer(issuer)
                .claim("name", name)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(expirySeconds)))
                .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claim);
            jwt.sign(signer);
            return jwt.serialize();
        }
        catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
	}
}
