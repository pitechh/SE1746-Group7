package com.example.accountservice.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.UUID;

@Configuration
public class SecurityJwtConfiguration {

    @Value("${app.security.access-token-private-key}")
    private String privateKeyString;

    @Value("${app.security.access-token-public-key}")
    private String publicKeyString;

    public static final String AUTHORITIES_KEY = "authorities";
    public static final String AUTHORITIEIDS_KEY = "authorieIds";
    public static final String ACCOUNT_KEY = "accountId";
    public static final String ROLE_DELIMITER = ",";

    @Bean
    public RSAKey generateRSAKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        byte[] privateKeyBytes = java.util.Base64.getDecoder().decode(privateKeyString);
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(publicKeyString);
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(generateRSAKey().toRSAPublicKey()).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefault());
        return jwtDecoder;
    }

    @Bean
    public JwtEncoder jwtEncoder() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(generateRSAKey())));
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimDelimiter(ROLE_DELIMITER);

//        grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIEIDS_KEY);
//        grantedAuthoritiesConverter.setAuthoritiesClaimName(ACCOUNT_KEY);
        grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}
