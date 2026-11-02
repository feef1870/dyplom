import { Component, inject, signal } from '@angular/core';
import { Order, WorkStep } from '../../models/models';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Api } from '../../services/api';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-order-detail-page',
  imports: [DatePipe, RouterLink],
  templateUrl: './order-detail-page.html',
  styleUrl: './order-detail-page.scss',
})
export class OrderDetailPage {
  private readonly api = inject(Api);
  private readonly route = inject(ActivatedRoute);

  order = signal<Order | undefined>(undefined);
  steps = signal<WorkStep[]>([]);
  error = signal<string | undefined>(undefined);

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadOrder(id);
        this.loadSteps(id);
      }
    });
  }

  private loadOrder(id: number): void {
    this.error.set(undefined);

    this.api.getOrder(id).subscribe({
      next: (order) => this.order.set(order),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load order.'),
    });
  }

  private loadSteps(id: number): void {
    this.api.getSteps('', 0, 100, id).subscribe({
      next: (page) => this.steps.set(page.content),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load steps.'),
    });
  }
}
