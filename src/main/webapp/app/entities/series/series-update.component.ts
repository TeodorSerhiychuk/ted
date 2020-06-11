import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { ISeries, Series } from 'app/shared/model/series.model';
import { SeriesService } from './series.service';

@Component({
  selector: 'jhi-series-update',
  templateUrl: './series-update.component.html'
})
export class SeriesUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    title: [],
    description: [],
    rating: []
  });

  constructor(protected seriesService: SeriesService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ series }) => {
      this.updateForm(series);
    });
  }

  updateForm(series: ISeries): void {
    this.editForm.patchValue({
      id: series.id,
      title: series.title,
      description: series.description,
      rating: series.rating
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const series = this.createFromForm();
    if (series.id !== undefined) {
      this.subscribeToSaveResponse(this.seriesService.update(series));
    } else {
      this.subscribeToSaveResponse(this.seriesService.create(series));
    }
  }

  private createFromForm(): ISeries {
    return {
      ...new Series(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      rating: this.editForm.get(['rating'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISeries>>): void {
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
}
