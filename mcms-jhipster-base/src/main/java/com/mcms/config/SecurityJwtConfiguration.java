package com.mcms.config;

import static com.mcms.security.SecurityUtils.JWT_ALGORITHM;
import static com.mcms.security.SecurityUtils.PASSWORD_CHANGED_AT_CLAIM;
import static com.mcms.security.SecurityUtils.USER_ID_CLAIM;

import com.mcms.domain.User;
import com.mcms.management.SecurityMetersService;
import com.mcms.repository.UserRepository;
import java.time.Instant;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class SecurityJwtConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityJwtConfiguration.class);

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    private final UserRepository userRepository;

    public SecurityJwtConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public JwtDecoder jwtDecoder(SecurityMetersService metersService) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();
        return token -> {
            try {
                Jwt jwt = jwtDecoder.decode(token);

                // Validate password changed timestamp
                Long userId = jwt.getClaim(USER_ID_CLAIM);
                Long pwdChangedAtMillis = jwt.getClaim(PASSWORD_CHANGED_AT_CLAIM);

                if (userId != null && pwdChangedAtMillis != null) {
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        Instant tokenPasswordChangedAt = Instant.ofEpochMilli(pwdChangedAtMillis);

                        // If user's password was changed after the token was issued, reject the token
                        if (user.getPasswordChangedAt() != null && user.getPasswordChangedAt().isAfter(tokenPasswordChangedAt)) {
                            LOG.warn("JWT rejected: password changed after token was issued for user {}", userId);
                            throw new JwtException("Token invalidated due to password change");
                        }
                    }
                }

                return jwt;
            } catch (Exception e) {
                if (e.getMessage().contains("Invalid signature")) {
                    metersService.trackTokenInvalidSignature();
                } else if (e.getMessage().contains("Jwt expired at")) {
                    metersService.trackTokenExpired();
                } else if (
                    e.getMessage().contains("Invalid JWT serialization") ||
                    e.getMessage().contains("Malformed token") ||
                    e.getMessage().contains("Invalid unsecured/JWS/JWE") ||
                    e.getMessage().contains("Token invalidated")
                ) {
                    metersService.trackTokenMalformed();
                } else {
                    LOG.error("Unknown JWT error {}", e.getMessage());
                }
                throw e;
            }
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
}
