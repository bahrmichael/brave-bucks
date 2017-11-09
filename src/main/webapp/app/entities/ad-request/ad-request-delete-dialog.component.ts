import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { AdRequest } from './ad-request.model';
import { AdRequestPopupService } from './ad-request-popup.service';
import { AdRequestService } from './ad-request.service';

@Component({
    selector: 'jhi-ad-request-delete-dialog',
    templateUrl: './ad-request-delete-dialog.component.html'
})
export class AdRequestDeleteDialogComponent {

    adRequest: AdRequest;

    constructor(
        private adRequestService: AdRequestService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.adRequestService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'adRequestListModification',
                content: 'Deleted an adRequest'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-ad-request-delete-popup',
    template: ''
})
export class AdRequestDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private adRequestPopupService: AdRequestPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.adRequestPopupService
                .open(AdRequestDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
