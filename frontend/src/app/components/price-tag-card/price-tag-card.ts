import { Component, input } from '@angular/core';
import { PriceTag } from '../../models/models';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-price-tag-card',
  imports: [CurrencyPipe],
  templateUrl: './price-tag-card.html',
  styleUrl: './price-tag-card.scss',
})
export class PriceTagCard {
  priceTag = input.required<PriceTag>();
}
