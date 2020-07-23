package com.nevzatcirak.example.oauth2.opa.api;

import java.util.Set;

/**
 * @author Nevzat ÇIRAK
 * @mail ncirak@havelsan.com.tr
 * Created by ncirak at 23/07/2020
 */
public interface TenantService {
    /**
     * Gets tenant details by using issuer uri
     *
     * @param issuer
     * @return Tenant Detail
     */
    Tenant getByIssuer(String issuer);

    /**
     * Gets tenant details by using jwk set uri
     *
     * @param jwkSetUri
     * @return Tenant Detail
     */
    Tenant getByJwkSetUri(String jwkSetUri);

    /**
     * Changes domain which is read from property with newDomainName
     *
     * @param currentDomain
     * @param newDomainName
     */
    void changeDomainName(String currentDomain, String newDomainName);

    /**
     * Gets set of issuer uri of each tenant
     *
     * @return Issuer Uris as String
     */
    Set<String> getIssuerUris();
}
