package com.example.oauth2.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {
    private final RSASSASigner signer;
    private final String issuer;
    private final long expirySeconds;

    public JwtService(
        @Value("${app.jwt.private-key}") String privateKeyBase64,
        @Value("${app.jwt.public-key}") String publicKeyBase64,
		@Value("${app.jwt.issuer:oauth2-demo}") String issuer,
		@Value("${app.jwt.expiry-seconds:3600}") long expirySeconds)
        throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) factory.generatePrivate(spec);
        this.signer = new RSASSASigner(privateKey);

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
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claim);
            jwt.sign(signer);
            return jwt.serialize();
        }
        catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign JWT", e);
        }
	}
}
