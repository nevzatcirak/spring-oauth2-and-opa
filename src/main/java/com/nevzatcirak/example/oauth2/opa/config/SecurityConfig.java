package com.nevzatcirak.example.oauth2.opa.config;

import com.nevzatcirak.example.oauth2.opa.security.GrantedAuthoritiesInjector;
import com.nevzatcirak.example.oauth2.opa.voter.OPAVoter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.client.RestTemplate;

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
    private String issuerUri;
    private String jwkSetUri;

    public SecurityConfig(@Value("${opa.url}") String opaUrl, @Value("${auth.issuer-uri}") String issuerUri,
                          @Value("${auth.jwk-set-uri}") String jwkSetUri, @Value("${auth.host}") String hostname) {
        String authHostname = System.getenv("AUTH_HOSTNAME");
        String opaHostname = System.getenv("OPA_HOSTNAME");
        if (!Objects.isNull(authHostname)) {
            this.jwkSetUri = jwkSetUri.replaceAll(hostname, authHostname);
            this.issuerUri = issuerUri.replaceAll(hostname, authHostname);
        } else {
            this.jwkSetUri = jwkSetUri;
            this.issuerUri = issuerUri;
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

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /************************Resource Server Configurations*********************************/

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

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
                                .jwt(jwt -> jwt
                                        .decoder(jwtDecoder())
                                        .jwkSetUri(jwkSetUri)
                                        .jwtAuthenticationConverter(grantedAuthoritiesInjectorConverter())
                                )

                );
    }
}
