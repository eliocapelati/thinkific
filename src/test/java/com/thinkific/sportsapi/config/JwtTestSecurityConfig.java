package com.thinkific.sportsapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@TestConfiguration
public class JwtTestSecurityConfig {
    static final String AUTH0_TOKEN = "token";
    static final String SUB = "sub";
    static final String AUTH0ID = "auth0|6152501302b3dd0071c1fbe2";
    static final String USER_EMAIL_KEY = "email";
    public static final String USER_EMAIL_VALUE = "user@email.com";


    @Bean
    @Primary
    @Profile("test")
    public ReactiveJwtDecoder jwtDecoderTest() {
        return token -> jwt();
    }

    public Mono<Jwt> jwt() {

        Map<String, Object> claims = Map.of(
                SUB, AUTH0ID,
                USER_EMAIL_KEY, USER_EMAIL_VALUE
        );


        return Mono.just(new Jwt(
                AUTH0_TOKEN,
                Instant.now(),
                Instant.now().plusSeconds(30),
                Map.of("alg", "none"),
                claims
        ));
    }

}

