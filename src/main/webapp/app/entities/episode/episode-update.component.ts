import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IEpisode, Episode } from 'app/shared/model/episode.model';
import { EpisodeService } from './episode.service';
import { ISeries } from 'app/shared/model/series.model';
import { SeriesService } from 'app/entities/series/series.service';
import { IProducer } from 'app/shared/model/producer.model';
import { ProducerService } from 'app/entities/producer/producer.service';

type SelectableEntity = ISeries | IProducer;

@Component({
  selector: 'jhi-episode-update',
  templateUrl: './episode-update.component.html'
})
export class EpisodeUpdateComponent implements OnInit {
  isSaving = false;
  series: ISeries[] = [];
  producers: IProducer[] = [];

  editForm = this.fb.group({
    id: [],
    title: [],
    description: [],
    date: [],
    series: [],
    createdBy: []
  });

  constructor(
    protected episodeService: EpisodeService,
    protected seriesService: SeriesService,
    protected producerService: ProducerService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ episode }) => {
      if (!episode.id) {
        const today = moment().startOf('day');
        episode.date = today;
      }

      this.updateForm(episode);

      this.seriesService.query().subscribe((res: HttpResponse<ISeries[]>) => (this.series = res.body || []));

      this.producerService.query().subscribe((res: HttpResponse<IProducer[]>) => (this.producers = res.body || []));
    });
  }

  updateForm(episode: IEpisode): void {
    this.editForm.patchValue({
      id: episode.id,
      title: episode.title,
      description: episode.description,
      date: episode.date ? episode.date.format(DATE_TIME_FORMAT) : null,
      series: episode.series,
      createdBy: episode.createdBy
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const episode = this.createFromForm();
    if (episode.id !== undefined) {
      this.subscribeToSaveResponse(this.episodeService.update(episode));
    } else {
      this.subscribeToSaveResponse(this.episodeService.create(episode));
    }
  }

  private createFromForm(): IEpisode {
    return {
      ...new Episode(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      description: this.editForm.get(['description'])!.value,
      date: this.editForm.get(['date'])!.value ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      series: this.editForm.get(['series'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEpisode>>): void {
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

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
