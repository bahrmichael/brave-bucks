import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { AdRequest } from './ad-request.model';
import { AdRequestPopupService } from './ad-request-popup.service';
import { AdRequestService } from './ad-request.service';

@Component({
    selector: 'jhi-ad-request-dialog',
    templateUrl: './ad-request-dialog.component.html'
})
export class AdRequestDialogComponent implements OnInit {

    adRequest: AdRequest;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private adRequestService: AdRequestService,
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
        if (this.adRequest.id !== undefined) {
            this.subscribeToSaveResponse(
                this.adRequestService.update(this.adRequest));
        } else {
            this.subscribeToSaveResponse(
                this.adRequestService.create(this.adRequest));
        }
    }

    private subscribeToSaveResponse(result: Observable<AdRequest>) {
        result.subscribe((res: AdRequest) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: AdRequest) {
        this.eventManager.broadcast({ name: 'adRequestListModification', content: 'OK'});
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
    selector: 'jhi-ad-request-popup',
    template: ''
})
export class AdRequestPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private adRequestPopupService: AdRequestPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.adRequestPopupService
                    .open(AdRequestDialogComponent as Component, params['id']);
            } else {
                this.adRequestPopupService
                    .open(AdRequestDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
