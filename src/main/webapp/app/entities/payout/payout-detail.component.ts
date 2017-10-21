import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { Payout } from './payout.model';
import { PayoutService } from './payout.service';

@Component({
    selector: 'jhi-payout-detail',
    templateUrl: './payout-detail.component.html'
})
export class PayoutDetailComponent implements OnInit, OnDestroy {

    payout: Payout;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private payoutService: PayoutService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPayouts();
    }

    load(id) {
        this.payoutService.find(id).subscribe((payout) => {
            this.payout = payout;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInPayouts() {
        this.eventSubscriber = this.eventManager.subscribe(
            'payoutListModification',
            (response) => this.load(this.payout.id)
        );
    }
}
