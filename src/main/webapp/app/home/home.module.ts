import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BraveBucksSharedModule } from '../shared';

import { HOME_ROUTE, HomeComponent } from './';
import { SponsoredComponent } from './sponsored/sponsored.component';
import { KillmailTableComponent } from './killmail-table/killmail-table.component';
import { ManagerOverviewComponent } from './manager-overview/manager-overview.component';
import { HunterHighscoreComponent } from './hunter-highscore/hunter-highscore.component';
import { IntroComponent } from './intro/intro.component';
import { KillboardTrackerInfoComponent } from './killboard-tracker-info/killboard-tracker-info.component';
import { WalletTrackerInfoComponent } from './wallet-tracker-info/wallet-tracker-info.component';
import { PayoutProgressComponent } from './payout-progress/payout-progress.component';
import { InfoBlockComponent } from './info-block/info-block.component';
import {RatterHighscoreComponent} from "./ratter-highscore/ratter-highscore.component";

@NgModule({
    imports: [
        BraveBucksSharedModule,
        RouterModule.forRoot([ HOME_ROUTE ], { useHash: true })
    ],
    declarations: [
        HomeComponent,
        SponsoredComponent,
        KillmailTableComponent,
        ManagerOverviewComponent,
        HunterHighscoreComponent,
        RatterHighscoreComponent,
        IntroComponent,
        KillboardTrackerInfoComponent,
        WalletTrackerInfoComponent,
        PayoutProgressComponent,
        InfoBlockComponent,
    ],
    entryComponents: [
    ],
    providers: [
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BraveBucksHomeModule {}
