import { Log, User, UserManager, SessionStatus } from 'oidc-client';

import { Constants } from '../helpers/Constants';

export class AuthService {
  public userManager: UserManager;

  constructor() {
    const settings = {
      authority: Constants.stsAuthority,
      client_id: Constants.clientId,
      redirect_uri: `${Constants.clientRoot}signin-callback.html`,
      silent_redirect_uri: `${Constants.clientRoot}`,
      // tslint:disable-next-line:object-literal-sort-keys
      post_logout_redirect_uri: `${Constants.clientRoot}`,
      response_type: 'code',
      scope: Constants.clientScope
    };
    this.userManager = new UserManager(settings);

    Log.logger = console;
    Log.level = Log.INFO;
  }

  public getUser(): Promise<User | null> {
    return this.userManager.getUser();
  }

  public async login(): Promise<void> {
    return await this.userManager.signinRedirect();
  }

  public renewToken(): Promise<User> {
    var user = this.userManager.signinSilent();
    new UserManager({}).signinSilentCallback().catch((err) => console.error(err));
    return user;
  }

  public logout(): Promise<void> {
    return this.userManager.signoutRedirect();
  }
}
