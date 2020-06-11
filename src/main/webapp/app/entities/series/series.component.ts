import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ISeries } from 'app/shared/model/series.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { SeriesService } from './series.service';
import { SeriesDeleteDialogComponent } from './series-delete-dialog.component';

@Component({
  selector: 'jhi-series',
  templateUrl: './series.component.html'
})
export class SeriesComponent implements OnInit, OnDestroy {
  series: ISeries[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected seriesService: SeriesService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.series = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.seriesService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<ISeries[]>) => this.paginateSeries(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.series = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInSeries();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ISeries): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInSeries(): void {
    this.eventSubscriber = this.eventManager.subscribe('seriesListModification', () => this.reset());
  }

  delete(series: ISeries): void {
    const modalRef = this.modalService.open(SeriesDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.series = series;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateSeries(data: ISeries[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.series.push(data[i]);
      }
    }
  }
}
