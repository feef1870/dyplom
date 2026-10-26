export type Status = 'PLANNED' | 'IN_PROGRESS' | 'CLOSED';

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface Worker {
  id: number;
  keycloakId: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface Task {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  status: Status;
  unitQuantity: number;
  priceSnapshot: number;
  workStepId: number;
}

export interface TaskRequest {
  name: string;
  description?: string;
  unitQuantity: number;
  priceTagId: number;
  workStepId: number;
}

export interface WorkStep {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  status: Status;
  orderId: number;
}

export interface WorkStepRequest {
  name: string;
  description?: string;
  orderId: number;
}

export interface Order {
  id: number;
  name: string;
  description: string;
  createdAt: string;
  status: Status;
  clientId: number;
}

export interface OrderRequest {
  name: string;
  description?: string;
  clientId: number;
}

export interface PriceTag {
  id: number;
  name: string;
  unitPrice: number;
  isActive: boolean;
}

export interface PriceTagRequest {
  name: string;
  unitPrice: number;
}

export interface UpdatePriceTagRequest {
  name?: string;
  unitPrice?: number;
  isActive?: boolean;
}
