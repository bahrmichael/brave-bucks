import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BraveBucksSharedModule } from '../shared';

import { RequestAd_ROUTE, RequestAdComponent } from './';

@NgModule({
    imports: [
        BraveBucksSharedModule,
        RouterModule.forRoot([ RequestAd_ROUTE ], { useHash: true })
    ],
    declarations: [
        RequestAdComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksRequestAdModule {}
