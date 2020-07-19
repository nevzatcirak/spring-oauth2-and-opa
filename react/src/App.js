import React, { useEffect, useState } from 'react';
import './App.css';
import auth from './oauth2/Auth';

function App() {
  const [tokens, setTokens] = useState(auth.config.providers.reduce((output, provider) => {
    console.log("Output: " + JSON.stringify(output));
    console.log("Provider: " + JSON.stringify(provider));
    return ({
    ...output,
    [provider.name]: provider.idToken || provider.accessToken || provider.code
  })}, {}));

  useEffect(() => {
    console.log("useEffect")
    const onAuth = (error, { provider }) => {
      if (error){
        console.error(error);
        localStorage.setItem("nev","error");
      } 
      else {
        localStorage.setItem("nev",JSON.stringify(auth.provider(provider)));
        const { idToken, accessToken, code } = auth.provider(provider);
        setTokens({
          ...tokens,
          [provider]: idToken || accessToken || code || null
        });
      }
    };

    auth.on('login', onAuth);
    auth.on('logout', onAuth);
  }, []);

  return (
    <div className="App">
      <div>
      <h1>User Info</h1>
      {Object.entries(tokens).map(([provider, token]) => (
        <div key={provider}>
          <h2>{provider}</h2>
          <p>{JSON.stringify(tokens)}</p>
          <button
            disabled={!token.expired}
            onClick={() => auth.login(provider)}
          >
              Login
          </button>
          <button
            disabled={token.expired}
            onClick={() => auth.logout(provider)}
          >
              Logout
          </button>
          <code>{JSON.stringify(token.user, null, 2)}</code>
        </div>
      ))}
      </div>
    </div>
  );
}

export default App;
