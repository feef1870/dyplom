import { Component, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Order, PriceTag, Task, WorkStep } from '../../models/models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-step-detail-page',
  imports: [DatePipe, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './step-detail-page.html',
  styleUrl: './step-detail-page.scss',
})
export class StepDetailPage {
  private readonly api = inject(Api);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  step = signal<WorkStep | undefined>(undefined);
  tasks = signal<Task[]>([]);
  error = signal<string | undefined>(undefined);
  actionError = signal<string | undefined>(undefined);

  priceTags = signal<PriceTag[]>([]);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);
  transitioning = signal(false);

  form = this.fb.group({
    name: ['', [Validators.required]],
    description: [''],
    unitQuantity: [null as number | null, [Validators.required, Validators.min(0)]],
    priceTagId: [null as number | null, [Validators.required]],
  });

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadStep(id);
        this.loadTasks(id);
      }
    });
    this.loadPriceTags();
  }

  private loadStep(id: number): void {
    this.error.set(undefined);

    this.api.getStep(id).subscribe({
      next: (step) => this.step.set(step),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load step.'),
    });
  }

  private loadTasks(id: number): void {
    this.api.getTasks('', 0, 100, id).subscribe({
      next: (page) => this.tasks.set(page.content),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load tasks.'),
    });
  }

  private loadPriceTags(): void {
    this.api.getPriceTags('', 0, 1000).subscribe({
      next: (page) => this.priceTags.set(page.content),
      error: () => this.priceTags.set([]),
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
    const unitQuantity = this.form.value.unitQuantity!;
    const priceTagId = this.form.value.priceTagId!;
    const workStepId = this.step()!.id;

    this.submitting.set(true);

    this.api.createTask({ name, description, unitQuantity, priceTagId, workStepId }).subscribe({
      next: () => this.saved(),
      error: (err) => this.saveFailed(err),
    });
  }

  private saved(): void {
    this.form.reset();
    this.submitting.set(false);
    this.loadTasks(this.step()!.id);
  }

  private saveFailed(err: any): void {
    this.formError.set(err?.error?.message ?? 'Could not create task.');
    this.submitting.set(false);
  }

  startStep(): void {
    this.transitioning.set(true);
    this.actionError.set(undefined);

    this.api.startStep(this.step()!.id).subscribe({
      next: (step) => {
        this.transitioning.set(false);
        this.loadStep(step.id);
        this.loadTasks(step.id);
      },
      error: (err) => {
        this.transitioning.set(false);
        this.actionError.set(err?.error?.message ?? 'Could not start step.');
      },
    });
  }

  completeStep(): void {
    this.transitioning.set(true);
    this.actionError.set(undefined);

    this.api.completeStep(this.step()!.id).subscribe({
      next: (step) => {
        this.transitioning.set(false);
        this.loadStep(step.id);
        this.loadTasks(step.id);
      },
      error: (err) => {
        this.transitioning.set(false);
        this.actionError.set(err?.error?.message ?? 'Could not complete step.');
      },
    });
  }
}
