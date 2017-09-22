import {Component, OnInit} from '@angular/core';
import { Http, Response } from '@angular/http'
import {Appraisal} from "./appraisal.model";
import { Observable } from 'rxjs/Observable';

@Component({
    selector: 'jhi-the-buyback',
    templateUrl: './the-buyback.component.html',
    styleUrls: [
        'the-buyback.css'
    ]

})
export class TheBuybackComponent implements OnInit {
    isLoadingAppraisal: boolean;
    submitDone: boolean;
    appraisal: Appraisal;

    constructor(
        private http: Http
    ) {
    }

    ngOnInit(): void {
        if (!this.appraisal) {
            this.appraisal = new Appraisal();
        }
    }

    executeAppraisal() {
        this.isLoadingAppraisal = true;
        this.submitDone = false;
        return this.executeRequest().subscribe((response) => {
            this.appraisal = response;
            this.isLoadingAppraisal = false;
        });
    }

    executeRequest(): Observable<Appraisal> {
        return this.http.post('api/appraisal', this.appraisal).map((res: Response) => {
            return res.json();
        });
    }
}
