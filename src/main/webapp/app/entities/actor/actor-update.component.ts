import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

import { IActor, Actor } from 'app/shared/model/actor.model';
import { ActorService } from './actor.service';
import { IMovie } from 'app/shared/model/movie.model';
import { MovieService } from 'app/entities/movie/movie.service';
import { IEpisode } from 'app/shared/model/episode.model';
import { EpisodeService } from 'app/entities/episode/episode.service';

type SelectableEntity = IMovie | IEpisode;

@Component({
  selector: 'jhi-actor-update',
  templateUrl: './actor-update.component.html'
})
export class ActorUpdateComponent implements OnInit {
  isSaving = false;
  movies: IMovie[] = [];
  episodes: IEpisode[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.maxLength(100)]],
    surname: [null, [Validators.required, Validators.maxLength(100)]],
    bio: [],
    photoURL: [],
    movies: [],
    episodes: []
  });

  constructor(
    protected actorService: ActorService,
    protected movieService: MovieService,
    protected episodeService: EpisodeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ actor }) => {
      this.updateForm(actor);

      this.movieService.query().subscribe((res: HttpResponse<IMovie[]>) => (this.movies = res.body || []));

      this.episodeService.query().subscribe((res: HttpResponse<IEpisode[]>) => (this.episodes = res.body || []));
    });
  }

  updateForm(actor: IActor): void {
    this.editForm.patchValue({
      id: actor.id,
      name: actor.name,
      surname: actor.surname,
      bio: actor.bio,
      photoURL: actor.photoURL,
      movies: actor.movies,
      episodes: actor.episodes
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const actor = this.createFromForm();
    if (actor.id !== undefined) {
      this.subscribeToSaveResponse(this.actorService.update(actor));
    } else {
      this.subscribeToSaveResponse(this.actorService.create(actor));
    }
  }

  private createFromForm(): IActor {
    return {
      ...new Actor(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      surname: this.editForm.get(['surname'])!.value,
      bio: this.editForm.get(['bio'])!.value,
      photoURL: this.editForm.get(['photoURL'])!.value,
      movies: this.editForm.get(['movies'])!.value,
      episodes: this.editForm.get(['episodes'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActor>>): void {
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

  getSelected(selectedVals: SelectableEntity[], option: SelectableEntity): SelectableEntity {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
