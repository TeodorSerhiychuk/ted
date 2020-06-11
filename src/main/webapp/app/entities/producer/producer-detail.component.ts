import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProducer } from 'app/shared/model/producer.model';

@Component({
  selector: 'jhi-producer-detail',
  templateUrl: './producer-detail.component.html'
})
export class ProducerDetailComponent implements OnInit {
  producer: IProducer | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ producer }) => (this.producer = producer));
  }

  previousState(): void {
    window.history.back();
  }
}
