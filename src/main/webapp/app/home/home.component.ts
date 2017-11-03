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
    systems: any[];
    payoutThreshold = 100000000;
    isFirstLogin: boolean;
    payoutRequested: boolean;
    monthAvailable: number;
    killmailAddedManually: boolean;
    killmailFailedMessage: string;
    killmailAlreadyExists: boolean;
    killmailAddedText: string;

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

    submitKillmail(link: string) {
        this.killmailAddedManually = false;
        this.killmailFailedMessage = null;
        this.killmailAlreadyExists = false;
        this.killmailAddedText = null;
        const killId = link.split('/kill/')[1].replace('/', '');
        this.http.post('/api/killmail/' + killId, null).subscribe((data) => {
            if (data.status === 201) {
                this.killmails.unshift(data.json());
            } else {
                this.killmailAddedText = data.text();
            }
            this.killmailAddedManually = true;
        }, (err) => {
            if (err.status === 409) {
                this.killmailAlreadyExists = true;
            } else {
                this.killmailFailedMessage = err.text();
            }
        });
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
        this.http.get('/api/public/solar-systems').subscribe((data) => {
            this.systems = data.json();
        });
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

    getDotlanLink(region: string) {
        const systemNames = [];
        this.systems.forEach(s => systemNames.push(s.systemName));
        return "http://evemaps.dotlan.net/map/" + region + '/' + systemNames.join();
    }

}
