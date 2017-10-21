import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { Donation } from './donation.model';
import { DonationService } from './donation.service';

@Component({
    selector: 'jhi-donation-detail',
    templateUrl: './donation-detail.component.html'
})
export class DonationDetailComponent implements OnInit, OnDestroy {

    donation: Donation;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private donationService: DonationService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInDonations();
    }

    load(id) {
        this.donationService.find(id).subscribe((donation) => {
            this.donation = donation;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInDonations() {
        this.eventSubscriber = this.eventManager.subscribe(
            'donationListModification',
            (response) => this.load(this.donation.id)
        );
    }
}
