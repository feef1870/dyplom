import { LogLevel } from 'angular-auth-oidc-client';

export const authConfig = {
  authority: 'http://localhost:8081/realms/dyplom',

  clientId: 'dyplom-frontend',

  redirectUrl: window.location.origin,
  postLogoutRedirectUri: window.location.origin,

  scope: 'openid profile email offline_access',

  responseType: 'code',

  silentRenew: true,
  useRefreshToken: true,
  renewTimeBeforeTokenExpiresInSeconds: 30,

  secureRoutes: ['http://localhost:8080/api/'],

  logLevel: LogLevel.Warn,
};
