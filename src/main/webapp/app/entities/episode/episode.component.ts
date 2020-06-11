import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IEpisode } from 'app/shared/model/episode.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { EpisodeService } from './episode.service';
import { EpisodeDeleteDialogComponent } from './episode-delete-dialog.component';

@Component({
  selector: 'jhi-episode',
  templateUrl: './episode.component.html'
})
export class EpisodeComponent implements OnInit, OnDestroy {
  episodes: IEpisode[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected episodeService: EpisodeService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.episodes = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.episodeService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<IEpisode[]>) => this.paginateEpisodes(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.episodes = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInEpisodes();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IEpisode): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInEpisodes(): void {
    this.eventSubscriber = this.eventManager.subscribe('episodeListModification', () => this.reset());
  }

  delete(episode: IEpisode): void {
    const modalRef = this.modalService.open(EpisodeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.episode = episode;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateEpisodes(data: IEpisode[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.episodes.push(data[i]);
      }
    }
  }
}
