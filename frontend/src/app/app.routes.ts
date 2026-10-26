import { Routes } from '@angular/router';
import { autoLoginPartialRoutesGuard } from 'angular-auth-oidc-client';

export const routes: Routes = [
  {
    path: '',
    canActivate: [autoLoginPartialRoutesGuard],
    loadComponent: () =>
      import('./components/home-page/home-page').then((m) => m.HomePage),
  },
];
