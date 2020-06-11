import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IEpisode, Episode } from 'app/shared/model/episode.model';
import { EpisodeService } from './episode.service';
import { EpisodeComponent } from './episode.component';
import { EpisodeDetailComponent } from './episode-detail.component';
import { EpisodeUpdateComponent } from './episode-update.component';

@Injectable({ providedIn: 'root' })
export class EpisodeResolve implements Resolve<IEpisode> {
  constructor(private service: EpisodeService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IEpisode> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((episode: HttpResponse<Episode>) => {
          if (episode.body) {
            return of(episode.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Episode());
  }
}

export const episodeRoute: Routes = [
  {
    path: '',
    component: EpisodeComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Episodes'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: EpisodeDetailComponent,
    resolve: {
      episode: EpisodeResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Episodes'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: EpisodeUpdateComponent,
    resolve: {
      episode: EpisodeResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Episodes'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: EpisodeUpdateComponent,
    resolve: {
      episode: EpisodeResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Episodes'
    },
    canActivate: [UserRouteAccessService]
  }
];
