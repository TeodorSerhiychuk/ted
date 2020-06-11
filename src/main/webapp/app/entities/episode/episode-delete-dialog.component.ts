import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IEpisode } from 'app/shared/model/episode.model';
import { EpisodeService } from './episode.service';

@Component({
  templateUrl: './episode-delete-dialog.component.html'
})
export class EpisodeDeleteDialogComponent {
  episode?: IEpisode;

  constructor(protected episodeService: EpisodeService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.episodeService.delete(id).subscribe(() => {
      this.eventManager.broadcast('episodeListModification');
      this.activeModal.close();
    });
  }
}
