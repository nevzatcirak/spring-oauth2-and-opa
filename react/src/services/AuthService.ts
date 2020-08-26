import { Log, User, UserManager, SessionStatus } from 'oidc-client';

import { Constants } from '../helpers/Constants';

export class AuthService {
  public userManager: UserManager;

  constructor() {
    const settings = {
      authority: Constants.stsAuthority,
      client_id: Constants.clientId,
      redirect_uri: `${Constants.clientRoot}signin-callback.html`,
      silent_redirect_uri: `${Constants.clientRoot}silent-renew.html`,
      // tslint:disable-next-line:object-literal-sort-keys
      post_logout_redirect_uri: `${Constants.clientRoot}`,
      response_type: 'code',
      scope: Constants.clientScope
    };
    this.userManager = new UserManager(settings);

    Log.logger = console;
    Log.level = Log.INFO;
  }

  /**
   * Gets User claims which are contains access_token, refresh_token, token_type
   * scope, profile etc..
   * Details are in the User Object
   */
  public getUser(): Promise<User | null> {
    return this.userManager.getUser();
  }

  /**
   * If you are not logged in, you will redirect to provider login uri
   */
  public async login(): Promise<void> {
    return await this.userManager.signinRedirect();
  }

  /**
   * Sends renew token request silently, after this request access will be
   * renewed.
   */
  public renewToken(): Promise<User> {
    return this.userManager.signinSilent();
  }

  /**
   * Sends logout request to the authentication provider.
   */
  public logout(): Promise<void> {
    return this.userManager.signoutRedirect();
  }
}
