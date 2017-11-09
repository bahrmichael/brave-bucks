import {Component, OnInit} from '@angular/core';
import {Http} from '@angular/http'
import {AdRequest} from "../entities/ad-request/ad-request.model";

@Component({
    selector: 'jhi-request-ad',
    templateUrl: './request-ad.component.html',
})
export class RequestAdComponent implements OnInit {
    isSaving: boolean;
    isSuccessful: boolean;
    errorText: string;
    adRequest: AdRequest;
    availableMonths: string[];
    availableMonthsLoaded: boolean;
    submittedAdvancedAd: boolean;

    constructor(
        private http: Http
    ) {
    }

    ngOnInit(): void {
        if (!this.adRequest) {
            this.adRequest = new AdRequest();
        }
        this.loadMonths();
    }

    private loadMonths() {
        this.http.get('/api/ad-requests/available-months').subscribe((data) => {
            this.availableMonths = data.json();
            this.availableMonthsLoaded = true;
        });
    }

    submit() {
        this.isSaving = true;
        this.isSuccessful = false;
        this.errorText = null;
        this.submittedAdvancedAd = false;
        this.http.post('/api/ad-requests', this.adRequest).subscribe(
            (data) => {
                this.isSuccessful = true;
                this.isSaving = false;
                if (this.adRequest.link) {
                    this.submittedAdvancedAd = true;
                }
                this.adRequest = new AdRequest();
                this.loadMonths();
            }, (err) => {
                this.errorText = err.text();
                this.isSaving = false;
            }
        );
    }
}
