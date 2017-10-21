import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Donation } from './donation.model';
import { DonationPopupService } from './donation-popup.service';
import { DonationService } from './donation.service';

@Component({
    selector: 'jhi-donation-delete-dialog',
    templateUrl: './donation-delete-dialog.component.html'
})
export class DonationDeleteDialogComponent {

    donation: Donation;

    constructor(
        private donationService: DonationService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.donationService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'donationListModification',
                content: 'Deleted an donation'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-donation-delete-popup',
    template: ''
})
export class DonationDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private donationPopupService: DonationPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.donationPopupService
                .open(DonationDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
