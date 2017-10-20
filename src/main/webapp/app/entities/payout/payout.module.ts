import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ThebuybackSharedModule } from '../../shared';
import {
    PayoutService,
    PayoutPopupService,
    PayoutComponent,
    PayoutDetailComponent,
    PayoutDialogComponent,
    PayoutPopupComponent,
    PayoutDeletePopupComponent,
    PayoutDeleteDialogComponent,
    payoutRoute,
    payoutPopupRoute,
    PayoutResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...payoutRoute,
    ...payoutPopupRoute,
];

@NgModule({
    imports: [
        ThebuybackSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        PayoutComponent,
        PayoutDetailComponent,
        PayoutDialogComponent,
        PayoutDeleteDialogComponent,
        PayoutPopupComponent,
        PayoutDeletePopupComponent,
    ],
    entryComponents: [
        PayoutComponent,
        PayoutDialogComponent,
        PayoutPopupComponent,
        PayoutDeleteDialogComponent,
        PayoutDeletePopupComponent,
    ],
    providers: [
        PayoutService,
        PayoutPopupService,
        PayoutResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ThebuybackPayoutModule {}
