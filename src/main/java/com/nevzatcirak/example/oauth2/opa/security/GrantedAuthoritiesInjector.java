package com.nevzatcirak.example.oauth2.opa.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Nevzat ÇIRAK
 */
public class GrantedAuthoritiesInjector implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Value("${auth.user-name-attribute}")
    private String usernameAttribute;

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (jwt.containsClaim(usernameAttribute)) {
            String username = (String) jwt.getClaims().get(usernameAttribute);
        }

        return authorities;
    }
}
