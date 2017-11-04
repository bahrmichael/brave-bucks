import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { BraveBucksTransactionModule } from './transaction/transaction.module';
import { BraveBucksPayoutModule } from './payout/payout.module';
import { BraveBucksSolarSystemModule } from './solar-system/solar-system.module';
import { BraveBucksDonationModule } from './donation/donation.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        BraveBucksTransactionModule,
        BraveBucksPayoutModule,
        BraveBucksSolarSystemModule,
        BraveBucksDonationModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksEntityModule {}
