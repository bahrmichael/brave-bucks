import {Component, OnInit} from '@angular/core';
import {ConfigService, Principal} from "../../shared";

@Component({
               selector: 'jhi-wallet-tracker-info', templateUrl: './wallet-tracker-info.component.html', styles: []
           })
export class WalletTrackerInfoComponent implements OnInit {

    walletUrl: string;
    showWalletInfo: boolean;

    constructor(private configService: ConfigService, private principal: Principal) {
        this.showWalletInfo = localStorage.getItem('brave-bucks-hide-wallet-info') === null;
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.configService.getWalletUrl().subscribe((data) => this.walletUrl = data + "-" + account.id);
        });
    }

}
