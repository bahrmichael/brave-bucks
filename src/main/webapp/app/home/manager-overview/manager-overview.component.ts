import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";
import {Principal} from "../../shared";

@Component({
               selector: 'jhi-manager-overview', templateUrl: './manager-overview.component.html', styles: []
           })
export class ManagerOverviewComponent implements OnInit {

    pendingAds = 0;
    countPending = 0;
    largeValues = 0;
    smallValues = 0;

    constructor(private http: Http, private principal: Principal) {
    }

    ngOnInit() {
        this.loadManagerData();
    }

    loadManagerData() {
        this.http.get('/api/ad-requests/pending').subscribe((data) => this.pendingAds = +data.text());
        this.http.get('/api/payouts/pending').subscribe((data) => this.countPending = +data.text());
        this.http.get('/api/payouts/large').subscribe((data) => this.largeValues = +data.text());
        this.http.get('/api/payouts/small').subscribe((data) => this.smallValues = +data.text());
    }

}
