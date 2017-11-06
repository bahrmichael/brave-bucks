import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BraveBucksSharedModule } from '../../shared';
import {
    AdRequestService,
    AdRequestPopupService,
    AdRequestComponent,
    AdRequestDetailComponent,
    AdRequestDialogComponent,
    AdRequestPopupComponent,
    AdRequestDeletePopupComponent,
    AdRequestDeleteDialogComponent,
    adRequestRoute,
    adRequestPopupRoute,
    AdRequestResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...adRequestRoute,
    ...adRequestPopupRoute,
];

@NgModule({
    imports: [
        BraveBucksSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        AdRequestComponent,
        AdRequestDetailComponent,
        AdRequestDialogComponent,
        AdRequestDeleteDialogComponent,
        AdRequestPopupComponent,
        AdRequestDeletePopupComponent,
    ],
    entryComponents: [
        AdRequestComponent,
        AdRequestDialogComponent,
        AdRequestPopupComponent,
        AdRequestDeleteDialogComponent,
        AdRequestDeletePopupComponent,
    ],
    providers: [
        AdRequestService,
        AdRequestPopupService,
        AdRequestResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksAdRequestModule {}
