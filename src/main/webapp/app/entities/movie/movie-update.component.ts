import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IMovie, Movie } from 'app/shared/model/movie.model';
import { MovieService } from './movie.service';
import { IProducer } from 'app/shared/model/producer.model';
import { ProducerService } from 'app/entities/producer/producer.service';

@Component({
  selector: 'jhi-movie-update',
  templateUrl: './movie-update.component.html'
})
export class MovieUpdateComponent implements OnInit {
  isSaving = false;
  producers: IProducer[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    description: [],
    country: [],
    releaseDate: [],
    income: [],
    rating: [],
    createdBy: []
  });

  constructor(
    protected movieService: MovieService,
    protected producerService: ProducerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ movie }) => {
      if (!movie.id) {
        const today = moment().startOf('day');
        movie.releaseDate = today;
      }

      this.updateForm(movie);

      this.producerService.query().subscribe((res: HttpResponse<IProducer[]>) => (this.producers = res.body || []));
    });
  }

  updateForm(movie: IMovie): void {
    this.editForm.patchValue({
      id: movie.id,
      name: movie.name,
      description: movie.description,
      country: movie.country,
      releaseDate: movie.releaseDate ? movie.releaseDate.format(DATE_TIME_FORMAT) : null,
      income: movie.income,
      rating: movie.rating,
      createdBy: movie.createdBy
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const movie = this.createFromForm();
    if (movie.id !== undefined) {
      this.subscribeToSaveResponse(this.movieService.update(movie));
    } else {
      this.subscribeToSaveResponse(this.movieService.create(movie));
    }
  }

  private createFromForm(): IMovie {
    return {
      ...new Movie(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
      country: this.editForm.get(['country'])!.value,
      releaseDate: this.editForm.get(['releaseDate'])!.value
        ? moment(this.editForm.get(['releaseDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      income: this.editForm.get(['income'])!.value,
      rating: this.editForm.get(['rating'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMovie>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: IProducer): any {
    return item.id;
  }
}
