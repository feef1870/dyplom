import { Component, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Client, Order, Status } from '../../models/models';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-orders-page',
  imports: [ReactiveFormsModule, FormsModule, RouterLink],
  templateUrl: './orders-page.html',
  styleUrl: './orders-page.scss',
})
export class OrdersPage {
  private readonly api = inject(Api);
  private readonly fb = inject(FormBuilder);

  orders = signal<Order[]>([]);
  loading = signal(false);
  error = signal<string | undefined>(undefined);
  searchString = signal('');
  statusFilter = signal<Status | ''>('');
  currentPage = signal(0);
  totalPages = signal(0);
  size = signal(10);

  clients = signal<Client[]>([]);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);

  form = this.fb.group({
    name: ['', [Validators.required]],
    description: [''],
    clientId: [null as number | null, [Validators.required]],
  });

  ngOnInit(): void {
    this.load();
    this.loadClients();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(undefined);

    const filter = this.statusFilter();
    const status: Status | undefined = filter === '' ? undefined : filter;

    this.api.getOrders(this.searchString(), this.currentPage(), this.size(), status).subscribe({
      next: (page) => {
        this.orders.set(page.content);
        this.currentPage.set(page.number);
        this.totalPages.set(page.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Could not load orders.');
        this.loading.set(false);
      },
    });
  }

  loadClients(): void {
    this.api.getClients('', 0, 1000).subscribe({
      next: (page) => this.clients.set(page.content),
      error: () => this.clients.set([]),
    });
  }

  search(): void {
    this.currentPage.set(0);
    this.load();
  }

  next(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((p) => p + 1);
      this.load();
    }
  }

  prev(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update((p) => p - 1);
      this.load();
    }
  }

  onSubmit(): void {
    this.formError.set(undefined);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const name = this.form.value.name!.trim();
    const description = this.form.value.description?.trim() || undefined;
    const clientId = this.form.value.clientId!;

    this.submitting.set(true);

    this.api.createOrder({ name, description, clientId }).subscribe({
      next: () => this.saved(),
      error: (err) => this.saveFailed(err),
    });
  }

  private saved(): void {
    this.form.reset();
    this.submitting.set(false);
    this.currentPage.set(0);
    this.load();
  }

  private saveFailed(err: any): void {
    this.formError.set(err?.error?.message ?? 'Could not create order.');
    this.submitting.set(false);
  }
}
