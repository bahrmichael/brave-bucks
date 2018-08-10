import {Component, OnInit} from '@angular/core';
import {JhiEventManager} from 'ng-jhipster';

import {Account, ConfigService, Principal} from '../shared';
import {Http} from "@angular/http";
import {AdRequest} from "../entities/ad-request/ad-request.model";
import {SolarSystem} from "../entities/solar-system/solar-system.model";

@Component({
               selector: 'jhi-home', templateUrl: './home.component.html', styleUrls: ['home.css']
           })
export class HomeComponent implements OnInit {
    account: Account;
    systemsCatch: SolarSystem[];
    systemsImpass: SolarSystem[];

    constructor(private principal: Principal, private eventManager: JhiEventManager, private http: Http) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', (message) => {
            this.principal.identity().then((account) => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

}
