import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'actor',
        loadChildren: () => import('./actor/actor.module').then(m => m.ImdbActorModule)
      },
      {
        path: 'role',
        loadChildren: () => import('./role/role.module').then(m => m.ImdbRoleModule)
      },
      {
        path: 'series',
        loadChildren: () => import('./series/series.module').then(m => m.ImdbSeriesModule)
      },
      {
        path: 'episode',
        loadChildren: () => import('./episode/episode.module').then(m => m.ImdbEpisodeModule)
      },
      {
        path: 'producer',
        loadChildren: () => import('./producer/producer.module').then(m => m.ImdbProducerModule)
      },
      {
        path: 'movie',
        loadChildren: () => import('./movie/movie.module').then(m => m.ImdbMovieModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class ImdbEntityModule {}
