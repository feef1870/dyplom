import { Routes } from '@angular/router';
import { autoLoginPartialRoutesGuard } from 'angular-auth-oidc-client';
import { managerGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [autoLoginPartialRoutesGuard],
    loadComponent: () => import('./pages/home-page/home-page').then((m) => m.HomePage),
  },
  {
    path: 'tasks',
    canActivate: [autoLoginPartialRoutesGuard],
    loadComponent: () => import('./pages/tasks-page/tasks-page').then((m) => m.TasksPage),
  },
  {
    path: 'orders',
    canActivate: [autoLoginPartialRoutesGuard, managerGuard],
    loadComponent: () => import('./pages/orders-page/orders-page').then((m) => m.OrdersPage),
  },
  {
    path: 'price-tags',
    canActivate: [autoLoginPartialRoutesGuard, managerGuard],
    loadComponent: () =>
      import('./pages/price-tags-page/price-tags-page').then((m) => m.PriceTagsPage),
  },
];
