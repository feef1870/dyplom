import { Component, computed, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Task, Worker } from '../../models/models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-task-detail-page',
  imports: [DatePipe, ReactiveFormsModule],
  templateUrl: './task-detail-page.html',
  styleUrl: './task-detail-page.scss',
})
export class TaskDetailPage {
  private readonly api = inject(Api);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  task = signal<Task | undefined>(undefined);
  error = signal<string | undefined>(undefined);

  calculatedPrice = computed(() => {
    const t = this.task();
    return t ? t.unitQuantity * t.priceSnapshot : 0;
  });
  assignedWorkers = computed(() => {
    const t = this.task();
    return t ? t.workers : undefined;
  })

  workers = signal<Worker[]>([]);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);

  workerControl = this.fb.control(null as number | null, Validators.required);

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadTask(id);
        this.loadReports(id);
        this.loadWorkers();
      }
    });
  }

  private loadTask(id: number): void {
    this.error.set(undefined);

    this.api.getTask(id).subscribe({
      next: (task) => this.task.set(task),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load task.'),
    });
  }

  private loadReports(id: number): void {
    //TODO: load reports
  }

  private loadWorkers(): void {
    this.api.getWorkers('', 0, 100).subscribe({
      next: (page) => this.workers.set(page.content),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load workers.'),
    });
  }

  assignWorker(workerId: number): void {
    this.submitting.set(true);

    this.api.assignWorker(this.task()!.id, workerId).subscribe({
      next: () => this.success(),
      error: (err) => this.failure(err),
    });
  }

  unassignWorker(workerId: number): void {
    this.submitting.set(true);

    this.api.unassignWorker(this.task()!.id, workerId).subscribe({
      next: () => this.success(),
      error: (err) => this.failure(err),
    });
  }

  private success(): void {
    this.workerControl.reset();
    this.submitting.set(false);

    const currentTaskId = this.task()?.id;
    if (currentTaskId) {
      this.loadTask(currentTaskId);
    }
  }

  private failure(err: any): void {
    this.formError.set(err?.error?.message ?? 'Something went wrong.');
    this.submitting.set(false);

    const currentTaskId = this.task()?.id;
    if (currentTaskId) {
      this.loadTask(currentTaskId);
    }
  }
}
