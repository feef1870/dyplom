import { Component, inject, signal } from '@angular/core';
import { Api } from '../../services/api';
import { Task } from '../../models/models';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-tasks-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './tasks-page.html',
  styleUrl: './tasks-page.scss',
})
export class TasksPage {
  private readonly api = inject(Api);

  tasks = signal<Task[]>([]);
  loading = signal(false);
  error = signal<string | undefined>(undefined);
  searchString = signal('');
  currentPage = signal(0);
  totalPages = signal(0);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set(undefined);

    this.api.getTasks(this.searchString(), this.currentPage()).subscribe({
      next: (page) => {
        this.tasks.set(page.content);
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
}
