# Spring Boot Resource Server Application with Keycloak and Open Policy Agent Configuration
Keycloak is used for OAuth 2.0 Authentication Server and OPA is used for Authorization. 
## Prerequisites

- Java (tested with 1.8)
- Maven (tested with 3.3.9)
- OPA (tested with 0.22.0)
- Keycloak (tested with 10.0.2)

## [Keycloak](https://www.keycloak.org/)
Spring boot application is just configured as resource server, so there is no need making someting in Keycloak. Necessary configs are defined in application.yml.

```bash
auth:
  realm: master
  issuer-uri: "http://localhost:8080/auth/realms/${auth.realm}"
  user-name-attribute: preferred_username

spring:
  security:
    oauth2:
      resourceserver:
        id: keycloak
        jwt:
          issuer-uri: ${auth.issuer-uri}
          jwk-set-uri: "${auth.issuer-uri}/protocol/openid-connect/certs"
```

## [Open Policy Agent](https://www.openpolicyagent.org/docs/latest/)
While configuring OPA in Spring Security. We are using AccessDecisionManager and AccessDecisionVoter feature. In this repository, There is a simple implementation of an [AccessDecisionVoter for Spring Security](https://docs.spring.io/spring-security/site/docs/4.2.4.RELEASE/reference/htmlsingle/#authz-voting-based) that uses OPA for making authorization decisions. 

The example below is a simplistic Java-based configuration that you can use to test the voter. You can find detailed security config class in [SecurityConfig](https://github.com/nevzatcirak/spring-oauth2-and-opa/blob/master/src/main/java/com/nevzatcirak/example/oauth2/opa/config/SecurityConfig.java) Class.

```java
package com.nevzatcirak.example.oauth2.opa.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;

import com.nevzatcirak.example.oauth2.opa.voter.OPAVoter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().accessDecisionManager(accessDecisionManager());
    }
    
    /*****************************Open Policy Agent Configuration****************************/
        @Bean
        public AccessDecisionManager accessDecisionManager() {
            List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays
                    .asList(new OPAVoter("http://localhost:8181/v1/data/http/authz/allow"));
            return new UnanimousBased(decisionVoters);
        }
    /***************************************************************************************/


}
```

Please, While using AccessDecisionVoter, getAuthority() method of GrantedAuthority must return null.
    
GrantedAuthority is an interface with only one method:

    String getAuthority();

## Test Auth 2.0 with React Client
[oidc-client](https://github.com/IdentityModel/oidc-client-js) is used to support OpenID Connect (OIDC) and OAuth2 protocol for client-side. This library supports for user session and access token management.
### Building the Source
   Go to react folder and follow these commands.
   ```bash
   npm install
   npm start     
   ```
   React frontend will be served at 3000 port.
   
## Test Auth 2.0 Postman Client
[Postman](https://www.postman.com/) with OAuth 2.0 feature might be used to test services. You can find Postman OAuth 2.0 configuration [here](https://learning.postman.com/docs/sending-requests/authorization/).

You must create client in Keycloak for Postman. You may go with Implicit Flow. You must define flow type while creating client in Keycloak.

The example below is a implicit flow;

![alt text](https://github.com/nevzatcirak/spring-oauth2-and-opa/blob/master/example/images/postman.png?raw=true)
