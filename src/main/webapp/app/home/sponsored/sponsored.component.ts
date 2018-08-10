import {Component, OnInit} from '@angular/core';
import {AdRequest} from "../../entities/ad-request/ad-request.model";
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-sponsored', templateUrl: './sponsored.component.html', styles: []
           })
export class SponsoredComponent implements OnInit {

    ad: AdRequest;

    constructor(private http: Http) {
    }

    ngOnInit() {
        this.http.get('/api/ad-requests/active').subscribe((data) => this.ad = data.json());
    }

}
