import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { AdRequest } from './ad-request.model';
import { AdRequestService } from './ad-request.service';

@Component({
    selector: 'jhi-ad-request-detail',
    templateUrl: './ad-request-detail.component.html'
})
export class AdRequestDetailComponent implements OnInit, OnDestroy {

    adRequest: AdRequest;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private adRequestService: AdRequestService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInAdRequests();
    }

    load(id) {
        this.adRequestService.find(id).subscribe((adRequest) => {
            this.adRequest = adRequest;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInAdRequests() {
        this.eventSubscriber = this.eventManager.subscribe(
            'adRequestListModification',
            (response) => this.load(this.adRequest.id)
        );
    }
}
