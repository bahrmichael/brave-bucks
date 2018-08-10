import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-killmail-table', templateUrl: './killmail-table.component.html', styles: []
           })
export class KillmailTableComponent implements OnInit {

    killmails: any[];

    killmailAddedManually: boolean;
    killmailFailedMessage: string;
    killmailAlreadyExists: boolean;
    killmailAddedText: string;

    constructor(private http: Http) {
    }

    ngOnInit() {
        this.http.get('/api/killmails').subscribe((data) => {
            this.killmails = data.json();
        });
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
}
