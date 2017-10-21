import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { SolarSystem } from './solar-system.model';
import { SolarSystemPopupService } from './solar-system-popup.service';
import { SolarSystemService } from './solar-system.service';

@Component({
    selector: 'jhi-solar-system-delete-dialog',
    templateUrl: './solar-system-delete-dialog.component.html'
})
export class SolarSystemDeleteDialogComponent {

    solarSystem: SolarSystem;

    constructor(
        private solarSystemService: SolarSystemService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.solarSystemService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'solarSystemListModification',
                content: 'Deleted an solarSystem'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-solar-system-delete-popup',
    template: ''
})
export class SolarSystemDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private solarSystemPopupService: SolarSystemPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.solarSystemPopupService
                .open(SolarSystemDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
