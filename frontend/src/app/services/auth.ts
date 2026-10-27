import { inject, Injectable, signal } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Api } from './api';
import { Worker } from '../models/worker';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly oidc = inject(OidcSecurityService);
  private readonly api = inject(Api);

  worker = signal<Worker | undefined>(undefined);
  roles = signal<string[]>([]);

  constructor() {
    this.api.me().subscribe((worker) => {
      this.worker.set(worker);
    });

    this.oidc.getPayloadFromAccessToken().subscribe((payload) => {
      this.roles.set(payload?.realm_access?.roles ?? []);
    });
  }

  hasRole(role: string): boolean {
    return this.roles().includes(role);
  }

  logout(): void {
    this.oidc.logoff().subscribe();
  }
}
