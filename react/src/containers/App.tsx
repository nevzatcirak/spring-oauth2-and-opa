import * as React from 'react';
import AppContent from '../components/AppContent';
import Header from '../components/Header';
import logo from '../infra.png';
import './App.css';

class App extends React.Component {
  public render() {
    return (
      <div className="App">
        <Header pageTitle="Welcome to OAuth 2.0 PoC App" logoSrc={logo} />
        <div className="container-fluid">
          <div className="row">
            <div className="col">
              <AppContent />
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default App;
