package com.nevzatcirak.example.oauth2.opa.config;

import com.nevzatcirak.example.oauth2.opa.api.TenantService;
import com.nevzatcirak.example.oauth2.opa.security.GrantedAuthoritiesInjector;
import com.nevzatcirak.example.oauth2.opa.security.TenantJWSKeySelector;
import com.nevzatcirak.example.oauth2.opa.security.TenantJwtIssuerValidator;
import com.nevzatcirak.example.oauth2.opa.voter.OPAVoter;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetAwareJWSKeySelector;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Nevzat ÇIRAK,
 * @mail nevzatcirak17@gmail.com
 * Created by nevzatcirak at 01/07/2020.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@PropertySource("classpath:application.yml")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private String opaUrl;
    private TenantService tenantService;

    @Autowired
    public SecurityConfig(@Value("${opa.url}") String opaUrl, @Value("${auth.host}") String hostname, TenantService tenantService) {
        this.tenantService = tenantService;
        String authHostname = System.getenv("AUTH_HOSTNAME");
        String opaHostname = System.getenv("OPA_HOSTNAME");
        if(!Objects.isNull(authHostname)){
            this.tenantService.changeDomainName(hostname, authHostname);
        }
        if (!Objects.isNull(opaHostname)) {
            this.opaUrl = opaUrl.replaceAll(hostname, opaHostname);
        } else {
            this.opaUrl = opaUrl;
        }
    }

    @Bean
    RestTemplate rest() {
        RestTemplate rest = new RestTemplate();
        rest.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return execution.execute(request, body);
            }

            if (!(authentication.getCredentials() instanceof AbstractOAuth2Token)) {
                return execution.execute(request, body);
            }

            AbstractOAuth2Token token = (AbstractOAuth2Token) authentication.getCredentials();
            request.getHeaders().setBearerAuth(token.getTokenValue());
            return execution.execute(request, body);
        });
        return rest;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(new ServletBearerExchangeFilterFunction())
                .build();
    }

    /************************Resource Server Configurations*********************************/

    @Bean
    Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesInjectorConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesInjector());
        return jwtAuthenticationConverter;
    }

    @Bean
    GrantedAuthoritiesInjector grantedAuthoritiesInjector() {
        return new GrantedAuthoritiesInjector();
    }

    /***************************************************************************************/

    /*****************************Open Policy Agent Configuration****************************/
    @Bean
    public AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays
                .asList(new OPAVoter(opaUrl));
        return new UnanimousBased(decisionVoters);
    }

    /***************************************************************************************/

    /*****************************Multi-Tenancy Configuration****************************/
    @Bean
    public JWTClaimsSetAwareJWSKeySelector jwtClaimsSetAwareJWSKeySelector() {
        return new TenantJWSKeySelector(tenantService);
    }

    @Bean
    public OAuth2TokenValidator oAuth2TokenValidator() {
        return new TenantJwtIssuerValidator(tenantService);
    }

    @Bean
    JWTProcessor jwtProcessor(JWTClaimsSetAwareJWSKeySelector keySelector) {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
                new DefaultJWTProcessor();
        jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
        return jwtProcessor;
    }

    @Bean
    JwtDecoder jwtDecoder(JWTProcessor jwtProcessor, OAuth2TokenValidator<Jwt> jwtValidator) {
        NimbusJwtDecoder decoder = new NimbusJwtDecoder(jwtProcessor);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>
                (JwtValidators.createDefault(), jwtValidator);
        decoder.setJwtValidator(validator);
        return decoder;
    }

    /***************************************************************************************/



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .headers().httpStrictTransportSecurity().disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .accessDecisionManager(accessDecisionManager())
                .and()
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer
                                .jwt(jwt ->
                                        jwt
                                                .jwtAuthenticationConverter(grantedAuthoritiesInjectorConverter())
                                                .decoder(jwtDecoder(jwtProcessor(jwtClaimsSetAwareJWSKeySelector()),oAuth2TokenValidator())))

                );
    }
}
