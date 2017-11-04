import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BraveBucksSharedModule } from '../../shared';
import {
    DonationService,
    DonationPopupService,
    DonationComponent,
    DonationDetailComponent,
    DonationDialogComponent,
    DonationPopupComponent,
    DonationDeletePopupComponent,
    DonationDeleteDialogComponent,
    donationRoute,
    donationPopupRoute,
    DonationResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...donationRoute,
    ...donationPopupRoute,
];

@NgModule({
    imports: [
        BraveBucksSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        DonationComponent,
        DonationDetailComponent,
        DonationDialogComponent,
        DonationDeleteDialogComponent,
        DonationPopupComponent,
        DonationDeletePopupComponent,
    ],
    entryComponents: [
        DonationComponent,
        DonationDialogComponent,
        DonationPopupComponent,
        DonationDeleteDialogComponent,
        DonationDeletePopupComponent,
    ],
    providers: [
        DonationService,
        DonationPopupService,
        DonationResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksDonationModule {}
