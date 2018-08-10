import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-payout-progress', templateUrl: './payout-progress.component.html', styles: []
           })
export class PayoutProgressComponent implements OnInit {

    potentialPayout: number;
    payoutThreshold = 100000000;
    payoutRequested: boolean;
    monthAvailable: number;

    constructor(private http: Http) {
    }

    ngOnInit() {
        // todo: maybe we need to wrap this to only fire after the account has been loaded?
        this.http.get('/api/stats/month-available').subscribe((data) => {
            this.monthAvailable = +data.text();
        });
        this.http.get('/api/stats/potentialPayout').subscribe((data) => {
            this.potentialPayout = +data.text();
        });
    }

    getBarWidth(payout: number) {
        if (!payout || payout && payout === 0) {
            return 0;
        }
        return payout / this.payoutThreshold * 100;
    }

    requestPayout() {
        this.http.put('/api/payouts/trigger', "").subscribe((data) => {
            this.potentialPayout = 0;
            this.payoutRequested = true;
        }, (err) => console.log(err));
    }

}
