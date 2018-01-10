import { Component, OnInit } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';

import { Account, Principal } from '../shared';
import {Http} from "@angular/http";
import {AdRequest} from "../entities/ad-request/ad-request.model";
import {SolarSystem} from "../entities/solar-system/solar-system.model";

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
    highscore: any[];
    systemsCatch: SolarSystem[];
    systemsImpass: SolarSystem[];
    payoutThreshold = 100000000;
    isFirstLogin: boolean;
    payoutRequested: boolean;
    monthAvailable: number;
    killmailAddedManually: boolean;
    killmailFailedMessage: string;
    killmailAlreadyExists: boolean;
    killmailAddedText: string;
    ad: AdRequest;
    pendingAds = 0;
    countPending = 0;
    largeValues = 0;
    smallValues = 0;

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
            if (this.account.authorities.indexOf('ROLE_MANAGER') !== -1) {
                this.loadManagerData();
            }
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

    loadManagerData() {
        this.http.get('/api/ad-requests/pending').subscribe(
            (data) => this.pendingAds = +data.text()
        );
        this.http.get('/api/payouts/pending').subscribe(
            (data) => this.countPending = +data.text()
        );
        this.http.get('/api/payouts/large').subscribe(
            (data) => this.largeValues = +data.text()
        );
        this.http.get('/api/payouts/small').subscribe(
            (data) => this.smallValues = +data.text()
        );
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
        this.http.get('/api/ad-requests/active').subscribe(
            (data) => this.ad = data.json()
        );
        this.http.get('/api/stats/month-available').subscribe((data) => {
            this.monthAvailable = +data.text();
        });
        this.http.get('/api/stats/potentialPayout').subscribe((data) => {
            this.potentialPayout = +data.text();
        });
        this.http.get('/api/stats/highscore').subscribe((data) => {
            this.highscore = data.json();
        });
        this.http.get('/api/killmails').subscribe((data) => {
            this.killmails = data.json();
        })
        this.http.get('/api/solar-systems/region/CATCH').subscribe((data) => {
            this.systemsCatch = data.json();
        });
        this.http.get('/api/solar-systems/region/IMPASS').subscribe((data) => {
            this.systemsImpass = data.json();
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
        if (region === 'Catch') {
            this.systemsCatch.forEach((s) => systemNames.push(s.systemName));
        } else {
            this.systemsImpass.forEach((s) => systemNames.push(s.systemName));
        }
        return "http://evemaps.dotlan.net/map/" + region + '/' + systemNames.join();
    }

}
