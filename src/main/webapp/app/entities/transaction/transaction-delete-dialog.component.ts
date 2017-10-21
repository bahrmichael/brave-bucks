import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Transaction } from './transaction.model';
import { TransactionPopupService } from './transaction-popup.service';
import { TransactionService } from './transaction.service';

@Component({
    selector: 'jhi-transaction-delete-dialog',
    templateUrl: './transaction-delete-dialog.component.html'
})
export class TransactionDeleteDialogComponent {

    transaction: Transaction;

    constructor(
        private transactionService: TransactionService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.transactionService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'transactionListModification',
                content: 'Deleted an transaction'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-transaction-delete-popup',
    template: ''
})
export class TransactionDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private transactionPopupService: TransactionPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.transactionPopupService
                .open(TransactionDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
