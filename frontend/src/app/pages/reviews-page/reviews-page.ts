import { Component, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { Report } from '../../models/models';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReportDialog } from '../../components/report-dialog/report-dialog';

@Component({
  selector: 'app-reviews-page',
  imports: [DatePipe, RouterLink, MatDialogModule],
  templateUrl: './reviews-page.html',
  styleUrl: './reviews-page.scss',
})
export class ReviewsPage {
  private readonly api = inject(Api);
  private readonly dialog = inject(MatDialog);

  reports = signal<Report[]>([]);
  loading = signal(false);
  error = signal<string | undefined>(undefined);
  currentPage = signal(0);
  totalPages = signal(0);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(undefined);

    this.api.getReports(undefined, undefined, 'PENDING', this.currentPage()).subscribe({
      next: (page) => {
        this.reports.set(page.content);
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

  accept(reportId: number): void {
    this.api.acceptReport(reportId).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not accept report.'),
    });
  }

  openRejectDialog(report: Report): void {
    const dialogRef = this.dialog.open(ReportDialog, {
      width: '480px',
      data: { title: 'Reject report', label: 'Feedback' },
    });

    dialogRef.afterClosed().subscribe((feedback?: string) => {
      if (!feedback) return;
      this.reject(report.id, feedback);
    });
  }

  private reject(reportId: number, feedback: string): void {
    this.api.rejectReport(reportId, { feedback }).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(err?.error?.message ?? 'Could not reject report.'),
    });
  }
}
