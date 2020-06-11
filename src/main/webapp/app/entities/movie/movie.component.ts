import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMovie } from 'app/shared/model/movie.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { MovieService } from './movie.service';
import { MovieDeleteDialogComponent } from './movie-delete-dialog.component';

@Component({
  selector: 'jhi-movie',
  templateUrl: './movie.component.html'
})
export class MovieComponent implements OnInit, OnDestroy {
  movies: IMovie[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected movieService: MovieService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.movies = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.movieService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe((res: HttpResponse<IMovie[]>) => this.paginateMovies(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.movies = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInMovies();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IMovie): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInMovies(): void {
    this.eventSubscriber = this.eventManager.subscribe('movieListModification', () => this.reset());
  }

  delete(movie: IMovie): void {
    const modalRef = this.modalService.open(MovieDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.movie = movie;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateMovies(data: IMovie[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.movies.push(data[i]);
      }
    }
  }
}
