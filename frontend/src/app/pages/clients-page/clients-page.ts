import { Component, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { Client } from '../../models/models';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClientCard } from '../../components/client-card/client-card';

@Component({
  selector: 'app-clients-page',
  imports: [ReactiveFormsModule, FormsModule, ClientCard],
  templateUrl: './clients-page.html',
  styleUrl: './clients-page.scss',
})
export class ClientsPage {
  private readonly api = inject(Api);
  private readonly fb = inject(FormBuilder);

  clients = signal<Client[]>([]);
  loading = signal(false);
  error = signal<string | undefined>(undefined);
  searchString = signal('');
  currentPage = signal(0);
  totalPages = signal(0);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);
  editingId = signal<number | undefined>(undefined);

  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    phone: ['', Validators.maxLength(20)],
    email: [
      '',
      {
        validators: [Validators.required, Validators.email],
        updateOn: 'blur',
      },
    ],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(undefined);

    this.api.getClients(this.searchString(), this.currentPage()).subscribe({
      next: (page) => {
        this.clients.set(page.content);
        this.currentPage.set(page.number);
        this.totalPages.set(page.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Could not load clients.');
        this.loading.set(false);
      },
    });
  }

  search() {
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

  startEdit(client: Client): void {
    this.editingId.set(client.id);
    this.formError.set(undefined);
    this.form.setValue({
      firstName: client.firstName,
      lastName: client.lastName,
      phone: client.phone ?? '',
      email: client.email,
    });
  }

  cancelEdit(): void {
    this.editingId.set(undefined);
    this.formError.set(undefined);
    this.form.reset();
  }

  onSubmit(): void {
    this.formError.set(undefined);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const firstName = this.form.value.firstName!.trim();
    const lastName = this.form.value.lastName!.trim();
    const phone = this.form.value.phone?.trim();
    const email = this.form.value.email!.trim();
    const id = this.editingId();

    this.submitting.set(true);

    if (id === undefined) {
      this.api
        .createClient({
          firstName,
          lastName,
          phone,
          email,
        })
        .subscribe({
          next: () => this.saved(),
          error: (err) => this.saveFailed(err),
        });
    } else {
      //TODO: update client
    }
  }

  private saved(): void {
    this.form.reset();
    this.editingId.set(undefined);
    this.submitting.set(false);
    this.currentPage.set(0);
    this.load();
  }

  private saveFailed(err: any): void {
    this.formError.set(err?.error?.message ?? 'Something went wrong');
    this.submitting.set(false);
  }
}
