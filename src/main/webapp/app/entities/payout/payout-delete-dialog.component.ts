import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Payout } from './payout.model';
import { PayoutPopupService } from './payout-popup.service';
import { PayoutService } from './payout.service';

@Component({
    selector: 'jhi-payout-delete-dialog',
    templateUrl: './payout-delete-dialog.component.html'
})
export class PayoutDeleteDialogComponent {

    payout: Payout;

    constructor(
        private payoutService: PayoutService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.payoutService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'payoutListModification',
                content: 'Deleted an payout'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-payout-delete-popup',
    template: ''
})
export class PayoutDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private payoutPopupService: PayoutPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.payoutPopupService
                .open(PayoutDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
