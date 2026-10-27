import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Auth } from '../services/auth';

export const managerGuard: CanActivateFn = () => {
  const auth = inject(Auth);
  if (auth.hasRole('manager')) {
    return true;
  }
  return inject(Router).createUrlTree(['/']);
};
