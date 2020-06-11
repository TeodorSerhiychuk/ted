import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IProducer } from 'app/shared/model/producer.model';
import { ProducerService } from './producer.service';

@Component({
  templateUrl: './producer-delete-dialog.component.html'
})
export class ProducerDeleteDialogComponent {
  producer?: IProducer;

  constructor(protected producerService: ProducerService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.producerService.delete(id).subscribe(() => {
      this.eventManager.broadcast('producerListModification');
      this.activeModal.close();
    });
  }
}
