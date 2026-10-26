import { Component, inject, signal } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { HttpClient } from '@angular/common/http';
import { Worker } from '../../models/worker';
import { Api } from '../../services/api';

@Component({
  selector: 'app-home-page',
  imports: [],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage {
  private readonly oidc = inject(OidcSecurityService);
  private readonly api = inject(Api);

  worker = signal<Worker | undefined>(undefined);
  roles = signal<string[]>([]);

  ngOnInit(): void {
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
