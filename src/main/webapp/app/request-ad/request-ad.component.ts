import {Component, OnInit} from '@angular/core';
import {Http} from '@angular/http'
import {AdRequest} from "../entities/ad-request/ad-request.model";

@Component({
    selector: 'jhi-request-ad',
    templateUrl: './request-ad.component.html',
    styleUrls: [
        'request-ad.css'
    ]

})
export class RequestAdComponent implements OnInit {
    isSaving: boolean;
    isSuccessful: boolean;
    errorText: string;
    adRequest: AdRequest;

    constructor(
        private http: Http
    ) {
    }

    ngOnInit(): void {
        if (!this.adRequest) {
            this.adRequest = new AdRequest();
        }
    }

    submit() {
        this.isSaving = true;
        this.isSuccessful = false;
        this.http.post('/api/ad-requests', this.adRequest).subscribe(
            (data) => {
                this.isSuccessful = true;
                this.isSaving = false;
                this.adRequest = new AdRequest();
            }, (err) => {
                this.errorText = err.text();
                this.isSaving = false;
            }
        );
    }
}
