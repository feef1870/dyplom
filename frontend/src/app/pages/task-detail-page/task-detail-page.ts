import { Component, computed, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Task, Worker, Report } from '../../models/models';
import { DatePipe } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReportDialog } from '../../components/report-dialog/report-dialog';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-task-detail-page',
  imports: [DatePipe, ReactiveFormsModule, MatDialogModule],
  templateUrl: './task-detail-page.html',
  styleUrl: './task-detail-page.scss',
})
export class TaskDetailPage {
  private readonly api = inject(Api);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private readonly dialog = inject(MatDialog);
  readonly auth = inject(Auth);

  task = signal<Task | undefined>(undefined);
  error = signal<string | undefined>(undefined);
  actionError = signal<string | undefined>(undefined);

  reports = signal<Report[]>([]);
  reportsLoading = signal(false);
  reportsError = signal<string | undefined>(undefined);

  calculatedPrice = computed(() => {
    const t = this.task();
    return t ? t.unitQuantity * t.priceSnapshot : 0;
  });
  assignedWorkers = computed(() => {
    return this.task()?.workers ?? [];
  });

  workers = signal<Worker[]>([]);

  formError = signal<string | undefined>(undefined);
  submitting = signal(false);
  transitioning = signal(false);

  workerControl = this.fb.control(null as number | null, Validators.required);

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadTask(id);
        this.loadReports(id);
        if (this.auth.hasRole('manager')) {
          this.loadWorkers();
        }
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

  private loadReports(taskId: number): void {
    this.reportsLoading.set(true);
    this.reportsError.set(undefined);

    this.api.getReports(taskId, undefined, undefined, 0, 100).subscribe({
      next: (page) => {
        this.reports.set(page.content);
        this.reportsLoading.set(false);
      },
      error: (err) => {
        this.reportsError.set(err?.error?.message ?? 'Could not load reports.');
        this.reportsLoading.set(false);
      },
    });
  }

  private loadWorkers(): void {
    this.api.getWorkers('', 0, 100).subscribe({
      next: (page) => this.workers.set(page.content),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not load workers.'),
    });
  }

  canEdit(report: Report): boolean {
    return report.status === 'REJECTED' && report.authorId === this.auth.worker()?.id;
  }

  openReportDialog(report?: Report): void {
    const dialogRef = this.dialog.open(ReportDialog, {
      width: '480px',
      data: report
        ? { title: 'Edit report', label: 'Comment', initialValue: report.comment }
        : { title: 'New report', label: 'Comment' },
    });

    dialogRef.afterClosed().subscribe((comment?: string) => {
      if (!comment) return;
      if (report) {
        this.updateReport(report.id, comment);
      } else {
        this.createReport(comment);
      }
    });
  }

  openRejectDialog(report: Report): void {
    const dialogRef = this.dialog.open(ReportDialog, {
      width: '480px',
      data: { title: 'Reject report', label: 'Feedback' },
    });

    dialogRef.afterClosed().subscribe((feedback?: string) => {
      if (!feedback) return;
      this.rejectReport(report.id, feedback);
    });
  }

  acceptReport(reportId: number): void {
    this.api.acceptReport(reportId).subscribe({
      next: () => this.loadReports(this.task()!.id),
      error: (err) => this.reportsError.set(err?.error?.message ?? 'Could not accept report.'),
    });
  }

  private createReport(comment: string): void {
    const taskId = this.task()?.id;
    if (!taskId) return;

    this.api.createReport({ comment, workTaskId: taskId }).subscribe({
      next: () => this.loadReports(taskId),
      error: (err) => this.reportsError.set(err?.error?.message ?? 'Could not create report.'),
    });
  }

  private updateReport(reportId: number, comment: string): void {
    this.api.updateReport(reportId, { comment }).subscribe({
      next: () => this.loadReports(this.task()!.id),
      error: (err) => this.reportsError.set(err?.error?.message ?? 'Could not update report.'),
    });
  }

  private rejectReport(reportId: number, feedback: string): void {
    this.api.rejectReport(reportId, { feedback }).subscribe({
      next: () => this.loadReports(this.task()!.id),
      error: (err) => this.reportsError.set(err?.error?.message ?? 'Could not reject report.'),
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

  startTask(): void {
    this.transitioning.set(true);
    this.actionError.set(undefined);

    this.api.startTask(this.task()!.id).subscribe({
      next: (task) => {
        this.transitioning.set(false);
        this.loadTask(task.id);
        this.loadReports(task.id);
      },
      error: (err) => {
        this.transitioning.set(false);
        this.actionError.set(err?.error?.message ?? 'Could not start task.');
      },
    });
  }

  completeTask(): void {
    this.transitioning.set(true);
    this.actionError.set(undefined);

    this.api.completeTask(this.task()!.id).subscribe({
      next: (task) => {
        this.transitioning.set(false);
        this.loadTask(task.id);
        this.loadReports(task.id);
      },
      error: (err) => {
        this.transitioning.set(false);
        this.actionError.set(err?.error?.message ?? 'Could not complete task.');
      },
    });
  }
}
