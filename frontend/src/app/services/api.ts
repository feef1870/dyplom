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
  WorkStep,
  WorkStepRequest,
  Worker,
  Client,
  ClientRequest,
  Status,
} from '../models/models';

const BASE_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class Api {
  private readonly http = inject(HttpClient);

  me(): Observable<Worker> {
    return this.http.get<Worker>(`${BASE_URL}/workers/me`);
  }

  getWorkers(search = '', page = 0, size = 10): Observable<Page<Worker>> {
    let params = this.pageParams(search, page, size);
    return this.http.get<Page<Worker>>(`${BASE_URL}/workers`, { params });
  }

  getTasks(search = '', page = 0, size = 10, workStepId?: number): Observable<Page<Task>> {
    let params = this.pageParams(search, page, size);
    if (workStepId !== undefined) {
      params = params.set('workStepId', String(workStepId));
    }
    return this.http.get<Page<Task>>(`${BASE_URL}/tasks`, { params });
  }

  getTask(id: number): Observable<Task> {
    return this.http.get<Task>(`${BASE_URL}/tasks/${id}`);
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

  getOrders(search = '', page = 0, size = 10, status?: Status): Observable<Page<Order>> {
    let params = this.pageParams(search, page, size);
    if (status !== undefined) {
      params = params.set('status', String(status));
    }
    return this.http.get<Page<Order>>(`${BASE_URL}/orders`, { params });
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${BASE_URL}/orders/${id}`);
  }

  createOrder(body: OrderRequest): Observable<Order> {
    return this.http.post<Order>(`${BASE_URL}/orders`, body);
  }

  getSteps(search = '', page = 0, size = 10, orderId?: number): Observable<Page<WorkStep>> {
    let params = this.pageParams(search, page, size);
    if (orderId !== undefined) {
      params = params.set('orderId', String(orderId));
    }
    return this.http.get<Page<WorkStep>>(`${BASE_URL}/steps`, { params });
  }

  getStep(id: number): Observable<WorkStep> {
    return this.http.get<WorkStep>(`${BASE_URL}/steps/${id}`);
  }

  createStep(body: WorkStepRequest): Observable<WorkStep> {
    return this.http.post<WorkStep>(`${BASE_URL}/steps`, body);
  }

  getPriceTags(search = '', page = 0, size = 10, active?: boolean): Observable<Page<PriceTag>> {
    let params = this.pageParams(search, page, size);
    if (active !== undefined) {
      params = params.set('active', String(active));
    }
    return this.http.get<Page<PriceTag>>(`${BASE_URL}/price-tags`, { params });
  }

  createPriceTag(body: PriceTagRequest): Observable<PriceTag> {
    return this.http.post<PriceTag>(`${BASE_URL}/price-tags`, body);
  }

  updatePriceTag(id: number, body: PriceTagRequest): Observable<PriceTag> {
    return this.http.put<PriceTag>(`${BASE_URL}/price-tags/${id}`, body);
  }

  activatePriceTag(id: number): Observable<PriceTag> {
    return this.http.post<PriceTag>(`${BASE_URL}/price-tags/${id}/activate`, null);
  }

  deactivatePriceTag(id: number): Observable<PriceTag> {
    return this.http.post<PriceTag>(`${BASE_URL}/price-tags/${id}/deactivate`, null);
  }

  getClients(search = '', page = 0, size = 10): Observable<Page<Client>> {
    return this.http.get<Page<Client>>(`${BASE_URL}/clients`, {
      params: this.pageParams(search, page, size),
    });
  }

  createClient(body: ClientRequest): Observable<Client> {
    return this.http.post<Client>(`${BASE_URL}/clients`, body);
  }

  startOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${BASE_URL}/orders/${id}/start`, null);
  }

  completeOrder(id: number): Observable<Order> {
    return this.http.post<Order>(`${BASE_URL}/orders/${id}/complete`, null);
  }

  startStep(id: number): Observable<WorkStep> {
    return this.http.post<WorkStep>(`${BASE_URL}/steps/${id}/start`, null);
  }

  completeStep(id: number): Observable<WorkStep> {
    return this.http.post<WorkStep>(`${BASE_URL}/steps/${id}/complete`, null);
  }

  startTask(id: number): Observable<Task> {
    return this.http.post<Task>(`${BASE_URL}/tasks/${id}/start`, null);
  }

  completeTask(id: number): Observable<Task> {
    return this.http.post<Task>(`${BASE_URL}/tasks/${id}/complete`, null);
  }

  private pageParams(search: string, page: number, size: number): HttpParams {
    return new HttpParams()
      .set('search', search)
      .set('page', String(page))
      .set('size', String(size));
  }
}
