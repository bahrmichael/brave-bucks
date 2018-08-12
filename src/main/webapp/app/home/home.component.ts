import {Component, OnInit} from '@angular/core';
import {JhiEventManager} from 'ng-jhipster';

import {Account, Principal} from '../shared';
import {Http} from "@angular/http";

@Component({
               selector: 'jhi-home', templateUrl: './home.component.html', styleUrls: ['home.css']
           })
export class HomeComponent implements OnInit {
    account: Account;

    constructor(private principal: Principal, private eventManager: JhiEventManager) {
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
