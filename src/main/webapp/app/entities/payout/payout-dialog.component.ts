import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Payout } from './payout.model';
import { PayoutPopupService } from './payout-popup.service';
import { PayoutService } from './payout.service';

@Component({
    selector: 'jhi-payout-dialog',
    templateUrl: './payout-dialog.component.html'
})
export class PayoutDialogComponent implements OnInit {

    payout: Payout;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private payoutService: PayoutService,
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
        if (this.payout.id !== undefined) {
            this.subscribeToSaveResponse(
                this.payoutService.update(this.payout));
        } else {
            this.subscribeToSaveResponse(
                this.payoutService.create(this.payout));
        }
    }

    private subscribeToSaveResponse(result: Observable<Payout>) {
        result.subscribe((res: Payout) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: Payout) {
        this.eventManager.broadcast({ name: 'payoutListModification', content: 'OK'});
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
    selector: 'jhi-payout-popup',
    template: ''
})
export class PayoutPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private payoutPopupService: PayoutPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.payoutPopupService
                    .open(PayoutDialogComponent as Component, params['id']);
            } else {
                this.payoutPopupService
                    .open(PayoutDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
