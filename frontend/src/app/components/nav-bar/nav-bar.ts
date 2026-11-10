import { Component, effect, inject, signal } from '@angular/core';
import { Auth } from '../../services/auth';
import { Api } from '../../services/api';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss',
})
export class NavBar {
  readonly auth = inject(Auth);
  private readonly api = inject(Api);

  pendingCount = signal(0);

  constructor() {
    effect(() => {
      if (this.auth.hasRole('supervisor')) {
        this.loadPendingCount();
      } else {
        this.pendingCount.set(0);
      }
    });
  }

  private loadPendingCount(): void {
    this.api.getReports(undefined, undefined, 'PENDING', 0, 1).subscribe({
      next: (page) => this.pendingCount.set(page.totalElements),
      error: () => this.pendingCount.set(0),
    });
  }
}
