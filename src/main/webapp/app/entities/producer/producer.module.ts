import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ImdbSharedModule } from 'app/shared/shared.module';
import { ProducerComponent } from './producer.component';
import { ProducerDetailComponent } from './producer-detail.component';
import { ProducerUpdateComponent } from './producer-update.component';
import { ProducerDeleteDialogComponent } from './producer-delete-dialog.component';
import { producerRoute } from './producer.route';

@NgModule({
  imports: [ImdbSharedModule, RouterModule.forChild(producerRoute)],
  declarations: [ProducerComponent, ProducerDetailComponent, ProducerUpdateComponent, ProducerDeleteDialogComponent],
  entryComponents: [ProducerDeleteDialogComponent]
})
export class ImdbProducerModule {}
