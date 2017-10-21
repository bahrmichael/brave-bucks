import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Donation } from './donation.model';
import { DonationPopupService } from './donation-popup.service';
import { DonationService } from './donation.service';

@Component({
    selector: 'jhi-donation-dialog',
    templateUrl: './donation-dialog.component.html'
})
export class DonationDialogComponent implements OnInit {

    donation: Donation;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private donationService: DonationService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.donation.id !== undefined) {
            this.subscribeToSaveResponse(
                this.donationService.update(this.donation));
        } else {
            this.subscribeToSaveResponse(
                this.donationService.create(this.donation));
        }
    }

    private subscribeToSaveResponse(result: Observable<Donation>) {
        result.subscribe((res: Donation) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Donation) {
        this.eventManager.broadcast({ name: 'donationListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-donation-popup',
    template: ''
})
export class DonationPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private donationPopupService: DonationPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.donationPopupService
                    .open(DonationDialogComponent as Component, params['id']);
            } else {
                this.donationPopupService
                    .open(DonationDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
