/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { BraveBucksTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { PayoutDetailComponent } from '../../../../../../main/webapp/app/entities/payout/payout-detail.component';
import { PayoutService } from '../../../../../../main/webapp/app/entities/payout/payout.service';
import { Payout } from '../../../../../../main/webapp/app/entities/payout/payout.model';

describe('Component Tests', () => {

    describe('Payout Management Detail Component', () => {
        let comp: PayoutDetailComponent;
        let fixture: ComponentFixture<PayoutDetailComponent>;
        let service: PayoutService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BraveBucksTestModule],
                declarations: [PayoutDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    PayoutService,
                    JhiEventManager
                ]
            }).overrideTemplate(PayoutDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(PayoutDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(PayoutService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Payout('aaa')));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.payout).toEqual(jasmine.objectContaining({id: 'aaa'}));
            });
        });
    });

});
