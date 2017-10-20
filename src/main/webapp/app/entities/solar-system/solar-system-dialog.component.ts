import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { SolarSystem } from './solar-system.model';
import { SolarSystemPopupService } from './solar-system-popup.service';
import { SolarSystemService } from './solar-system.service';

@Component({
    selector: 'jhi-solar-system-dialog',
    templateUrl: './solar-system-dialog.component.html'
})
export class SolarSystemDialogComponent implements OnInit {

    solarSystem: SolarSystem;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private solarSystemService: SolarSystemService,
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
        if (this.solarSystem.id !== undefined) {
            this.subscribeToSaveResponse(
                this.solarSystemService.update(this.solarSystem));
        } else {
            this.subscribeToSaveResponse(
                this.solarSystemService.create(this.solarSystem));
        }
    }

    private subscribeToSaveResponse(result: Observable<SolarSystem>) {
        result.subscribe((res: SolarSystem) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
    }

    private onSaveSuccess(result: SolarSystem) {
        this.eventManager.broadcast({ name: 'solarSystemListModification', content: 'OK'});
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
    selector: 'jhi-solar-system-popup',
    template: ''
})
export class SolarSystemPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private solarSystemPopupService: SolarSystemPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.solarSystemPopupService
                    .open(SolarSystemDialogComponent as Component, params['id']);
            } else {
                this.solarSystemPopupService
                    .open(SolarSystemDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
