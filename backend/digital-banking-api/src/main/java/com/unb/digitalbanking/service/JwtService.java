package com.unb.digitalbanking.service;

import com.unb.digitalbanking.entity.AppUser;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(AppUser user) {

        Instant issuedAt = Instant.now();
        Instant expiresAt =
                issuedAt.plusSeconds(expirationSeconds);

        JwtClaimsSet.Builder claimsBuilder =
                JwtClaimsSet.builder()
                        .issuer(issuer)
                        .subject(user.getUsername())
                        .id(UUID.randomUUID().toString())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claim(
                                "roles",
                                List.of("ROLE_" + user.getRole())
                        );

        if (user.getCustomerId() != null) {
            claimsBuilder.claim(
                    "customerId",
                    user.getCustomerId()
            );
        }

        JwtClaimsSet claims = claimsBuilder.build();

        JwsHeader header =
                JwsHeader.with(MacAlgorithm.HS256)
                        .type("JWT")
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(
                                header,
                                claims
                        )
                )
                .getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}