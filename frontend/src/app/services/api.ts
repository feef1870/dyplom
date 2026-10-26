import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Order,
  OrderRequest,
  Page,
  PriceTag,
  PriceTagRequest,
  Task,
  TaskRequest,
  UpdatePriceTagRequest,
  Worker,
  WorkStep,
  WorkStepRequest,
} from '../models/worker';

const BASE_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class Api {
  private readonly http = inject(HttpClient);

  me(): Observable<Worker> {
    return this.http.get<Worker>(`${BASE_URL}/workers/me`);
  }

  getTasks(search = '', page = 0, size = 10): Observable<Page<Task>> {
    return this.http.get<Page<Task>>(`${BASE_URL}/tasks`, {
      params: this.pageParams(search, page, size),
    });
  }

  createTask(body: TaskRequest): Observable<Task> {
    return this.http.post<Task>(`${BASE_URL}/tasks`, body);
  }

  assignWorker(taskId: number, workerId: number): Observable<Task> {
    return this.http.put<Task>(`${BASE_URL}/tasks/${taskId}/workers/${workerId}`, null);
  }

  unassignWorker(taskId: number, workerId: number): Observable<Task> {
    return this.http.delete<Task>(`${BASE_URL}/tasks/${taskId}/workers/${workerId}`);
  }

  getOrders(search = '', page = 0, size = 10): Observable<Page<Order>> {
    return this.http.get<Page<Order>>(`${BASE_URL}/orders`, {
      params: this.pageParams(search, page, size),
    });
  }

  createOrder(body: OrderRequest): Observable<Order> {
    return this.http.post<Order>(`${BASE_URL}/orders`, body);
  }

  getSteps(search = '', page = 0, size = 10): Observable<Page<WorkStep>> {
    return this.http.get<Page<WorkStep>>(`${BASE_URL}/steps`, {
      params: this.pageParams(search, page, size),
    });
  }

  createStep(body: WorkStepRequest): Observable<WorkStep> {
    return this.http.post<WorkStep>(`${BASE_URL}/steps`, body);
  }

  getPriceTags(search = '', page = 0, size = 10): Observable<Page<PriceTag>> {
    return this.http.get<Page<PriceTag>>(`${BASE_URL}/price-tags`, {
      params: this.pageParams(search, page, size),
    });
  }

  createPriceTag(body: PriceTagRequest): Observable<PriceTag> {
    return this.http.post<PriceTag>(`${BASE_URL}/price-tags`, body);
  }

  updatePriceTag(id: number, body: UpdatePriceTagRequest): Observable<PriceTag> {
    return this.http.patch<PriceTag>(`${BASE_URL}/price-tags/${id}`, body);
  }

  deletePriceTag(id: number): Observable<void> {
    return this.http.delete<void>(`${BASE_URL}/price-tags/${id}`);
  }

  private pageParams(search: string, page: number, size: number): HttpParams {
    return new HttpParams()
      .set('search', search)
      .set('page', String(page))
      .set('size', String(size));
  }
}
