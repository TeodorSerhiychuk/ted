import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { ImdbSharedModule } from 'app/shared/shared.module';
import { ImdbCoreModule } from 'app/core/core.module';
import { ImdbAppRoutingModule } from './app-routing.module';
import { ImdbHomeModule } from './home/home.module';
import { ImdbEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    ImdbSharedModule,
    ImdbCoreModule,
    ImdbHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    ImdbEntityModule,
    ImdbAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class ImdbAppModule {}
