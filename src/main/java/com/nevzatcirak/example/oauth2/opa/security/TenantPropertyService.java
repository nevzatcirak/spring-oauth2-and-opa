package com.nevzatcirak.example.oauth2.opa.security;

import com.nevzatcirak.example.oauth2.opa.api.Tenant;
import com.nevzatcirak.example.oauth2.opa.api.TenantService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Nevzat ÇIRAK
 * @mail ncirak@havelsan.com.tr
 * Created by ncirak at 22/07/2020
 */
@Component
@ConfigurationProperties("multi-tenant")
public class TenantPropertyService implements TenantService {
    private List<Tenant> tenants = new ArrayList<>();

    public TenantPropertyService() {
//        tenantMap = new ConcurrentHashMap<>();
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    @Override
    public Tenant getByIssuer(String issuer) {
        for (Tenant tenant : this.tenants) {
            if (tenant.getIssuerUri().equals(issuer))
                return tenant;
        }
        return null;
    }

    @Override
    public Tenant getByJwkSetUri(String jwkSetUri) {
        for (Tenant tenant : this.tenants) {
            if (tenant.getJwkSetUri().equals(jwkSetUri))
                return tenant;
        }
        return null;
    }

    @Override
    public void changeDomainName(String currentDomain, String newDomainName) {
        this.tenants.forEach(tenant -> this.changeTenantDomainName(tenant, currentDomain, newDomainName));
    }

    @Override
    public Set<String> getIssuerUris() {
        Set<String> issuerUris = new HashSet<>();
        this.tenants.forEach(tenant -> issuerUris.add(tenant.getIssuerUri()));
        return issuerUris;
    }

    private void changeTenantDomainName(Tenant tenant, String currentDomain, String newDomainName) {
        tenant.setIssuerUri(
                tenant.getIssuerUri().replaceAll(currentDomain, newDomainName)
        );
        tenant.setJwkSetUri(
                tenant.getJwkSetUri().replaceAll(currentDomain, newDomainName)
        );
    }

}
