import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { ISeries, Series } from 'app/shared/model/series.model';
import { SeriesService } from './series.service';
import { SeriesComponent } from './series.component';
import { SeriesDetailComponent } from './series-detail.component';
import { SeriesUpdateComponent } from './series-update.component';

@Injectable({ providedIn: 'root' })
export class SeriesResolve implements Resolve<ISeries> {
  constructor(private service: SeriesService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISeries> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((series: HttpResponse<Series>) => {
          if (series.body) {
            return of(series.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Series());
  }
}

export const seriesRoute: Routes = [
  {
    path: '',
    component: SeriesComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Series'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SeriesDetailComponent,
    resolve: {
      series: SeriesResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Series'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: SeriesUpdateComponent,
    resolve: {
      series: SeriesResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Series'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SeriesUpdateComponent,
    resolve: {
      series: SeriesResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Series'
    },
    canActivate: [UserRouteAccessService]
  }
];
