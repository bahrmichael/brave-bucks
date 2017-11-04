import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BraveBucksSharedModule } from '../../shared';
import {
    SolarSystemService,
    SolarSystemPopupService,
    SolarSystemComponent,
    SolarSystemDialogComponent,
    SolarSystemPopupComponent,
    SolarSystemDeletePopupComponent,
    SolarSystemDeleteDialogComponent,
    solarSystemRoute,
    solarSystemPopupRoute,
    SolarSystemResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...solarSystemRoute,
    ...solarSystemPopupRoute,
];

@NgModule({
    imports: [
        BraveBucksSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        SolarSystemComponent,
        SolarSystemDialogComponent,
        SolarSystemDeleteDialogComponent,
        SolarSystemPopupComponent,
        SolarSystemDeletePopupComponent,
    ],
    entryComponents: [
        SolarSystemComponent,
        SolarSystemDialogComponent,
        SolarSystemPopupComponent,
        SolarSystemDeleteDialogComponent,
        SolarSystemDeletePopupComponent,
    ],
    providers: [
        SolarSystemService,
        SolarSystemPopupService,
        SolarSystemResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksSolarSystemModule {}
