import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BraveBucksSharedModule } from '../shared';

import { TheBuyback_ROUTE, TheBuybackComponent } from './';

@NgModule({
    imports: [
        BraveBucksSharedModule,
        RouterModule.forRoot([ TheBuyback_ROUTE ], { useHash: true })
    ],
    declarations: [
        TheBuybackComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksTheBuybackModule {}
