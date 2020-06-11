import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ImdbSharedModule } from 'app/shared/shared.module';
import { SeriesComponent } from './series.component';
import { SeriesDetailComponent } from './series-detail.component';
import { SeriesUpdateComponent } from './series-update.component';
import { SeriesDeleteDialogComponent } from './series-delete-dialog.component';
import { seriesRoute } from './series.route';

@NgModule({
  imports: [ImdbSharedModule, RouterModule.forChild(seriesRoute)],
  declarations: [SeriesComponent, SeriesDetailComponent, SeriesUpdateComponent, SeriesDeleteDialogComponent],
  entryComponents: [SeriesDeleteDialogComponent]
})
export class ImdbSeriesModule {}
