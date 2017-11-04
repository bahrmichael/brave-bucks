/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { BraveBucksTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { DonationDetailComponent } from '../../../../../../main/webapp/app/entities/donation/donation-detail.component';
import { DonationService } from '../../../../../../main/webapp/app/entities/donation/donation.service';
import { Donation } from '../../../../../../main/webapp/app/entities/donation/donation.model';

describe('Component Tests', () => {

    describe('Donation Management Detail Component', () => {
        let comp: DonationDetailComponent;
        let fixture: ComponentFixture<DonationDetailComponent>;
        let service: DonationService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BraveBucksTestModule],
                declarations: [DonationDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    DonationService,
                    JhiEventManager
                ]
            }).overrideTemplate(DonationDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(DonationDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(DonationService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Donation('aaa')));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.donation).toEqual(jasmine.objectContaining({id: 'aaa'}));
            });
        });
    });

});
