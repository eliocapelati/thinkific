package com.thinkific.sportsapi.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;

import java.util.Collections;
import java.util.Map;

public class Auth0ClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>>{

    public static final String DEFAULT_VALUE = "";
    private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {

        final Map<String, Object> convertedClaims = this.delegate.convert(claims);

        final Object email = convertedClaims.getOrDefault("https://thinkific.com/email", DEFAULT_VALUE);
        final Object userName = convertedClaims.getOrDefault("https://thinkific.com/username", DEFAULT_VALUE);

        convertedClaims.put("email", email);
        convertedClaims.put("username", userName);

        return convertedClaims;
    }

}
