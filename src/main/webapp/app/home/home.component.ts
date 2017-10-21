import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';

import { Account, Principal } from '../shared';
import {Http} from "@angular/http";

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: [
        'home.css'
    ]
})
export class HomeComponent implements OnInit {
    account: Account;
    potentialPayout: number;
    killmails: any[];
    payoutThreshold = 100000000;
    isFirstLogin: boolean;
    payoutRequested: boolean;
    monthAvailable: number;

    constructor(
        private principal: Principal,
        private eventManager: JhiEventManager,
        private http: Http
    ) {
        this.isFirstLogin = localStorage.getItem('brave-nukem-first-login') === null;
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
            this.getData();
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
                this.getData();
            });
        });
    }

    requestPayout() {
        this.http.put('/api/payouts/trigger', "").subscribe(
            (data) => {
                this.potentialPayout = 0;
                this.payoutRequested = true;
            },
            (err) => console.log(err)
        );
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    getData() {
        this.http.get('/api/stats/month-available').subscribe((data) => {
            this.monthAvailable = +data.text();
        });
        this.http.get('/api/stats/potentialPayout').subscribe((data) => {
            this.potentialPayout = +data.text();
        });
        this.http.get('/api/killmails').subscribe((data) => {
            this.killmails = data.json();
        })
    }

    getBarWidth(payout: number) {
        if (!payout || payout && payout === 0) {
            return 0;
        }
        return payout / this.payoutThreshold * 100;
    }

    setFirstLogin() {
        localStorage.setItem('brave-nukem-first-login', 'done');
        this.isFirstLogin = false;
    }
}
