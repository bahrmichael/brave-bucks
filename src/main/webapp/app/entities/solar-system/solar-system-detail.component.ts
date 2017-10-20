import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { SolarSystem } from './solar-system.model';
import { SolarSystemService } from './solar-system.service';

@Component({
    selector: 'jhi-solar-system-detail',
    templateUrl: './solar-system-detail.component.html'
})
export class SolarSystemDetailComponent implements OnInit, OnDestroy {

    solarSystem: SolarSystem;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private solarSystemService: SolarSystemService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSolarSystems();
    }

    load(id) {
        this.solarSystemService.find(id).subscribe((solarSystem) => {
            this.solarSystem = solarSystem;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSolarSystems() {
        this.eventSubscriber = this.eventManager.subscribe(
            'solarSystemListModification',
            (response) => this.load(this.solarSystem.id)
        );
    }
}
