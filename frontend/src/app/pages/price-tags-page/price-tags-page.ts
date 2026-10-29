import { Component, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { PriceTag } from '../../models/models';
import { PriceTagCard } from '../../components/price-tag-card/price-tag-card';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-price-tags-page',
  imports: [PriceTagCard, FormsModule, ReactiveFormsModule],
  templateUrl: './price-tags-page.html',
  styleUrl: './price-tags-page.scss',
})
export class PriceTagsPage {
  private readonly api = inject(Api);
  private readonly fb = inject(FormBuilder);

  priceTags = signal<PriceTag[]>([]);
  loading = signal(false);
  error = signal<string | undefined>(undefined);
  searchString = signal('');
  currentPage = signal(0);
  totalPages = signal(0);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);
  editingId = signal<number | undefined>(undefined);

  form = this.fb.group({
    name: ['', [Validators.required]],
    unitPrice: [null as number | null, [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(undefined);

    this.api.getPriceTags(this.searchString(), this.currentPage()).subscribe({
      next: (page) => {
        this.priceTags.set(page.content);
        this.currentPage.set(page.number);
        this.totalPages.set(page.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message ?? 'Something went wrong');
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

  startEdit(priceTag: PriceTag): void {
    this.editingId.set(priceTag.id);
    this.formError.set(undefined);
    this.form.setValue({ name: priceTag.name, unitPrice: priceTag.unitPrice });
  }

  cancelEdit(): void {
    this.editingId.set(undefined);
    this.formError.set(undefined);
    this.form.reset();
  }

  toggleActive(priceTag: PriceTag): void {
    const request = priceTag.isActive
      ? this.api.deactivatePriceTag(priceTag.id)
      : this.api.activatePriceTag(priceTag.id);

    request.subscribe({
      next: (updated) => {
        this.priceTags.update((list) => list.map((p) => (p.id === updated.id ? updated : p)));
      },
      error: (err) => this.formError.set(err?.error?.message ?? 'Something went wrong'),
    });
  }

  onSubmit(): void {
    this.formError.set(undefined);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const name = this.form.value.name!.trim();
    const unitPrice = this.form.value.unitPrice!;
    const id = this.editingId();

    this.submitting.set(true);

    if (id === undefined) {
      this.api.createPriceTag({ name, unitPrice }).subscribe({
        next: () => this.saved(),
        error: (err) => this.saveFailed(err),
      });
    } else {
      this.api.updatePriceTag(id, { name, unitPrice }).subscribe({
        next: () => this.saved(),
        error: (err) => this.saveFailed(err),
      });
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
