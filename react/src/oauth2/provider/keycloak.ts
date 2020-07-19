import { OpenIDProvider, OAuth2Provider } from '@salte-auth/salte-auth';

export class Keycloak extends OpenIDProvider {
    constructor(config: Keycloak.Config) {
        super(config);
      }
    
      /**
       * This is the default name of the provider.
       */
      get name() {
        return 'keycloak';
      }
    
      /**
       * This should use `this.config.url` to build the provider-specific login url.
       */
      get login() {
        // In this case Keycloak's "/authorize" is right at the root and 
        // it supports a custom audience parameter.
        return this.url(`${this.config.url}/protocol/openid-connect/auth`, {
          audience: this.config.audience
        });
      }
    
      /**
       * This should use `this.config.url` to build the provider-specific logout url.
       */
      get logout() {
        return this.url(`${this.config.url}/protocol/openid-connect/logout`, {
          redirect_uri: this.config.redirectUrl,
          client_id: this.config.clientID
        });
      }
}

export interface Keycloak {
    config: Keycloak.Config;
  }
  
  export declare namespace Keycloak {
    export interface Config extends OpenIDProvider.Config {
      url: string;   
    }
  }