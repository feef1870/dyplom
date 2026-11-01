import { Component, input } from '@angular/core';
import { Client } from '../../models/models';

@Component({
  selector: 'app-client-card',
  imports: [],
  templateUrl: './client-card.html',
  styleUrl: './client-card.scss',
})
export class ClientCard {
  client = input.required<Client>();
}
