import * as React from 'react';
import { UserManager } from 'oidc-client';

import { ToastContainer, toast } from 'react-toastify';
import { ApiService } from '../services/ApiService';
import { AuthService } from '../services/AuthService';

import AuthContent from './AuthContent';
import Buttons from './Buttons';

export default class AppContent extends React.Component<any, any> {
  public authService: AuthService;
  public apiService: ApiService;
  private shouldCancel: boolean;

  constructor(props: any) {
    super(props);

    this.authService = new AuthService();
    this.apiService = new ApiService();
    this.state = { user: {}, api: {} };
    this.shouldCancel = false;
  }

  public componentDidMount() {
    this.getUser();
  }

  public login = () => {
    this.authService.login();
  };

  public callApi = () => {
    this.apiService
      .callApi('test')
      .then(data => {
        this.setState({ api: {...this.state.api, callApi: data.data } });
        toast.success('Api return successfully data, check in section - Api response');
      })
      .catch(error => {
        toast.error(error.message);
      });
  };

  public callApi2 = () => {
    this.apiService
      .callApi('rest/test')
      .then(data => {
        this.setState({ api: {...this.state.api, callApi2: data.data } });
        toast.success('Api return successfully data, check in section - Api response');
      })
      .catch(error => {
        toast.error(error.message);
      });
  };

  public callApi3 = () => {
    this.apiService
      .callApi('rest/test2')
      .then(data => {
        this.setState({ api: {...this.state.api, callApi3: data.data } });
        toast.success('Api return successfully data, check in section - Api response');
      })
      .catch(error => {
        toast.error(error.message);
      });
  };

  public componentWillUnmount() {
    this.shouldCancel = true;
  }

  public renewToken = () => {
    this.authService
      .renewToken()
      .then(user => {
        toast.success('Token has been sucessfully renewed. :-)');
        this.getUser();
      })
      .catch(error => {
        toast.error(error);
      });
  };

  public logout = () => {
    this.authService.logout();
  };

  public getUser = () => {
    this.authService.getUser().then(user => {
      if (user) {
        toast.success('User has been successfully loaded from store.');
      } else {
        toast.info('You are not logged in.');
      }

      if (!this.shouldCancel) {
        this.setState({ user });
      }
    });
  };

  public render() {
    return (
      <>
        <ToastContainer />

        <Buttons
          login={this.login}
          logout={this.logout}
          renewToken={this.renewToken}
          getUser={this.getUser}
          callApi={this.callApi}
          callApi2={this.callApi2}
          callApi3={this.callApi3}
        />

        <AuthContent api={this.state.api} user={this.state.user} />
      </>
    );
  }
}
