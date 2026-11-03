import { Component, inject, signal } from '@angular/core';
import { Order, WorkStep } from '../../models/models';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Api } from '../../services/api';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-order-detail-page',
  imports: [DatePipe, RouterLink, ReactiveFormsModule],
  templateUrl: './order-detail-page.html',
  styleUrl: './order-detail-page.scss',
})
export class OrderDetailPage {
  private readonly api = inject(Api);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  order = signal<Order | undefined>(undefined);
  steps = signal<WorkStep[]>([]);
  error = signal<string | undefined>(undefined);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);

  form = this.fb.group({
    name: ['', Validators.required],
    description: [''],
  });

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

  onSubmit(): void {
    this.formError.set(undefined);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const name = this.form.value.name!.trim();
    const description = this.form.value.description?.trim() || undefined;
    const orderId = this.order()!.id;

    this.submitting.set(true);

    this.api.createStep({ name, description, orderId }).subscribe({
      next: () => this.saved(),
      error: (err) => this.saveFailed(err),
    });
  }

  private saved(): void {
    this.form.reset();
    this.submitting.set(false);
    this.loadSteps(this.order()!.id);
  }

  private saveFailed(err: any): void {
    this.formError.set(err?.error?.message ?? 'Could not create step.');
    this.submitting.set(false);
  }
}
