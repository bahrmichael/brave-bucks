import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-hunter-highscore', templateUrl: './hunter-highscore.component.html', styles: []
           })
export class HunterHighscoreComponent implements OnInit {

    highscore: any[];

    constructor(private http: Http) {
    }

    ngOnInit() {
        this.http.get('/api/stats/highscore').subscribe((data) => {
            this.highscore = data.json();
        });
    }

}
