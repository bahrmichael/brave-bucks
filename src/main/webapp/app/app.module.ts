import './vendor.ts';

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { Ng2Webstorage } from 'ng2-webstorage';

import { BraveBucksSharedModule, UserRouteAccessService } from './shared';
import { BraveBucksHomeModule } from './home/home.module';
import { BraveBucksAdminModule } from './admin/admin.module';
import { BraveBucksAccountModule } from './account/account.module';
import { BraveBucksEntityModule } from './entities/entity.module';

import { customHttpProvider } from './blocks/interceptor/http.provider';
import { PaginationConfig } from './blocks/config/uib-pagination.config';

// jhipster-needle-angular-add-module-import JHipster will add new module here

import {
    JhiMainComponent,
    LayoutRoutingModule,
    NavbarComponent,
    FooterComponent,
    ProfileService,
    PageRibbonComponent,
    ErrorComponent
} from './layouts';
import {SsoComponent} from "./layouts/callback/sso.component";
import {BraveBucksRequestAdModule} from "./request-ad/request-ad.module";

@NgModule({
    imports: [
        BrowserModule,
        LayoutRoutingModule,
        Ng2Webstorage.forRoot({ prefix: 'jhi', separator: '-'}),
        BraveBucksSharedModule,
        BraveBucksHomeModule,
        BraveBucksAdminModule,
        BraveBucksAccountModule,
        BraveBucksEntityModule,
        BraveBucksRequestAdModule
        // jhipster-needle-angular-add-module JHipster will add new module here
    ],
    declarations: [
        JhiMainComponent,
        NavbarComponent,
        ErrorComponent,
        PageRibbonComponent,
        FooterComponent,
        SsoComponent
    ],
    providers: [
        ProfileService,
        customHttpProvider(),
        PaginationConfig,
        UserRouteAccessService
    ],
    bootstrap: [ JhiMainComponent ]
})
export class BraveBucksAppModule {}
