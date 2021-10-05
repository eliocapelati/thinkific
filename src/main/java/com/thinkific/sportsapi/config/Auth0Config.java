package com.thinkific.sportsapi.config;

import com.auth0.client.auth.AuthAPI;
import com.thinkific.sportsapi.config.properties.Auth0Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Auth0Properties.class)
public class Auth0Config {

    private final Auth0Properties properties;

    public Auth0Config(Auth0Properties properties) {
        this.properties = properties;
    }

    @Bean
    public AuthAPI configure() {
        return new AuthAPI(properties.getDomain(), properties.getClientId(), properties.getClientSecret());
    }
}
