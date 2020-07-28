package com.nevzatcirak.example.oauth2.opa.api;

/**
 * @author Nevzat ÇIRAK
 * @mail ncirak@havelsan.com.tr
 * Created by ncirak at 28/07/2020
 */
public interface AuthFacade {
    /**
     * Extract username from SecurityContextHolder
     *
     * @return username
     */
    String getUsername();

    /**
     * Extract email from SecurityContextHolder
     *
     * @return email or null
     */
    String getEmail();

    /**
     * Gets principal object from SecurityContextHolder.
     * ex: Jwt for OAuth 2.0/Openid Resource Server, UserDetails for Cas Authentication etc.
     *
     * @return Principal Object
     */
    Object getPrincipal();

    /**
     * Gets user info details from SecurityContextHolder or KAPI.
     * While using Cas Authentication, User Data will be loaded by SecurityContextHolder.
     * For OAuth 2.0/Openid, It will be looaded by requesting from KAPI.
     * 
     * @return Kapi User Data
     */
    //User getUser();
}
