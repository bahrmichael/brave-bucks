import {Component, OnInit} from '@angular/core';

@Component({
               selector: 'jhi-killboard-tracker-info', templateUrl: './killboard-tracker-info.component.html', styles: []
           })
export class KillboardTrackerInfoComponent implements OnInit {

    isFirstLogin: boolean;

    constructor() {
        this.isFirstLogin = localStorage.getItem('brave-bucks-first-login') === null;
    }

    ngOnInit() {
    }

    setFirstLogin() {
        localStorage.setItem('brave-bucks-first-login', 'done');
        this.isFirstLogin = false;
    }

}
