package com.nevzatcirak.example.oauth2.opa.service;

import com.nevzatcirak.example.oauth2.opa.api.AuthFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Nevzat ÇIRAK
 * @mail ncirak@havelsan.com.tr
 * Created by ncirak at 28/07/2020
 */
@Service
public class OpenidAuthFacade implements AuthFacade {
    private String usernameAttribute;

    public OpenidAuthFacade(@Value("${auth.user-name-attribute}") String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    @Override
    public String getUsername() {
        Optional<Jwt> jwtOptional = this.getOpenidPrincipal();
        if (jwtOptional.isPresent())
            return jwtOptional.map(this::getUsernameFromClaims).get();
        return null;
    }

    @Override
    public String getEmail() {
        Optional<Jwt> jwtOptional = this.getOpenidPrincipal();
        if (jwtOptional.isPresent())
            return jwtOptional.map(this::getEmailFromClaims).get();
        return null;
    }

    @Override
    public Object getPrincipal() {
        Optional<Jwt> jwtPrincipalOptional = this.getOpenidPrincipal();
        return jwtPrincipalOptional.get();
    }

    private Optional<Jwt> getOpenidPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(authentication)) {
            return Optional.of((Jwt) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    private String getUsernameFromClaims(Jwt jwt) {
        return (String) jwt.getClaims().get(this.usernameAttribute);
    }

    private String getEmailFromClaims(Jwt jwt) {
        return (String) jwt.getClaims().get("email");
    }
}
