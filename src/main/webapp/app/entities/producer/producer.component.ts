import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IProducer } from 'app/shared/model/producer.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ProducerService } from './producer.service';
import { ProducerDeleteDialogComponent } from './producer-delete-dialog.component';

@Component({
  selector: 'jhi-producer',
  templateUrl: './producer.component.html'
})
export class ProducerComponent implements OnInit, OnDestroy {
  producers: IProducer[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected producerService: ProducerService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.producers = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.producerService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<IProducer[]>) => this.paginateProducers(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.producers = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInProducers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IProducer): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInProducers(): void {
    this.eventSubscriber = this.eventManager.subscribe('producerListModification', () => this.reset());
  }

  delete(producer: IProducer): void {
    const modalRef = this.modalService.open(ProducerDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.producer = producer;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateProducers(data: IProducer[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.producers.push(data[i]);
      }
    }
  }
}
