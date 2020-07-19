import { SalteAuth } from '@salte-auth/salte-auth';
import { Redirect } from '@salte-auth/redirect';
import { Keycloak } from './provider/keycloak';

const Auth = new SalteAuth({
    providers: [
        new Keycloak({
            clientID: 'react',
            responseType: 'code',
            url: 'http://localhost:8080/auth/realms/x-realm',
            routes: true,
            scope: 'email'
        })
    ],

    handlers: [
        new Redirect({
            default: true
        })
    ]
});

export default Auth;



