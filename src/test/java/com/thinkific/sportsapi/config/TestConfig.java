package com.thinkific.sportsapi.config;

import com.github.javafaker.Faker;
import com.thinkific.sportsapi.service.AuthService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    AuthService createAuth() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    Faker createFaker(){
        return new Faker();
    }
}
