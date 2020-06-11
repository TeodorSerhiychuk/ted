import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IActor } from 'app/shared/model/actor.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ActorService } from './actor.service';
import { ActorDeleteDialogComponent } from './actor-delete-dialog.component';

@Component({
  selector: 'jhi-actor',
  templateUrl: './actor.component.html'
})
export class ActorComponent implements OnInit, OnDestroy {
  actors: IActor[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected actorService: ActorService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.actors = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.actorService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<IActor[]>) => this.paginateActors(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.actors = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInActors();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IActor): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInActors(): void {
    this.eventSubscriber = this.eventManager.subscribe('actorListModification', () => this.reset());
  }

  delete(actor: IActor): void {
    const modalRef = this.modalService.open(ActorDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.actor = actor;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateActors(data: IActor[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.actors.push(data[i]);
      }
    }
  }
}
