package com.thinkific.sportsapi.config.security;

import com.thinkific.sportsapi.config.properties.Auth0Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {


    private final Auth0Properties auth0Properties;

    public WebSecurityConfig(Auth0Properties auth0Properties) {
        this.auth0Properties = auth0Properties;
    }

    @Bean
    public SecurityWebFilterChain springWebFilterChain(final ServerHttpSecurity http) {

        final String[] permitPost = {
                "/v1/users", "/v1/users/login",
                "/v1/users/", "/v1/users/login/"
        };

        //Set up the server layer
        http
                .httpBasic().disable()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .requestCache().requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        //Set up the application layer
        http
                .authorizeExchange(c -> c
                        .pathMatchers(HttpMethod.POST, permitPost).permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer().jwt();

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.PUT.name(),
                HttpMethod.POST.name(),
                HttpMethod.DELETE.name()
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
        return source;
    }


    @Bean
    @Profile("!test")
    public ReactiveJwtDecoder jwtDecoder() {

        var jwtDecoder =
                (NimbusReactiveJwtDecoder) ReactiveJwtDecoders.fromOidcIssuerLocation(auth0Properties.getIssuerUri());

        var audienceValidator = new OAuth2AudienceValidator(auth0Properties.getAudience());
        var withIssuer = JwtValidators.createDefaultWithIssuer(auth0Properties.getIssuerUri());
        var withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);


        jwtDecoder.setJwtValidator(withAudience);
        jwtDecoder.setClaimSetConverter(new Auth0ClaimAdapter());


        return jwtDecoder;
    }

}
