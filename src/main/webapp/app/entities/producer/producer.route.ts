import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IProducer, Producer } from 'app/shared/model/producer.model';
import { ProducerService } from './producer.service';
import { ProducerComponent } from './producer.component';
import { ProducerDetailComponent } from './producer-detail.component';
import { ProducerUpdateComponent } from './producer-update.component';

@Injectable({ providedIn: 'root' })
export class ProducerResolve implements Resolve<IProducer> {
  constructor(private service: ProducerService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProducer> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((producer: HttpResponse<Producer>) => {
          if (producer.body) {
            return of(producer.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Producer());
  }
}

export const producerRoute: Routes = [
  {
    path: '',
    component: ProducerComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Producers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ProducerDetailComponent,
    resolve: {
      producer: ProducerResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Producers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ProducerUpdateComponent,
    resolve: {
      producer: ProducerResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Producers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ProducerUpdateComponent,
    resolve: {
      producer: ProducerResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'Producers'
    },
    canActivate: [UserRouteAccessService]
  }
];
