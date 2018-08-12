import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-ratter-highscore', templateUrl: './ratter-highscore.component.html', styles: []
           })
export class RatterHighscoreComponent implements OnInit {

    highscore: any[];

    constructor(private http: Http) {
    }

    ngOnInit() {
        this.http.get('/api/stats/highscore/RATTING').subscribe((data) => {
            this.highscore = data.json();
        });
    }

}
