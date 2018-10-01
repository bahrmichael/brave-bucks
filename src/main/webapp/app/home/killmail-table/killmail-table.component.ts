import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-killmail-table', templateUrl: './killmail-table.component.html', styles: []
           })
export class KillmailTableComponent implements OnInit {

    killmails: any[];

    constructor(private http: Http) {
    }

    ngOnInit() {
        this.http.get('/api/killmails').subscribe((data) => {
            this.killmails = data.json();
        });
    }

}
