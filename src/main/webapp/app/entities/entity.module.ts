import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { ThebuybackTransactionModule } from './transaction/transaction.module';
import { ThebuybackPayoutModule } from './payout/payout.module';
import { ThebuybackSolarSystemModule } from './solar-system/solar-system.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        ThebuybackTransactionModule,
        ThebuybackPayoutModule,
        ThebuybackSolarSystemModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ThebuybackEntityModule {}
