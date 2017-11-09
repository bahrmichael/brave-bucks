/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { BraveBucksTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { AdRequestDetailComponent } from '../../../../../../main/webapp/app/entities/ad-request/ad-request-detail.component';
import { AdRequestService } from '../../../../../../main/webapp/app/entities/ad-request/ad-request.service';
import { AdRequest } from '../../../../../../main/webapp/app/entities/ad-request/ad-request.model';

describe('Component Tests', () => {

    describe('AdRequest Management Detail Component', () => {
        let comp: AdRequestDetailComponent;
        let fixture: ComponentFixture<AdRequestDetailComponent>;
        let service: AdRequestService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [BraveBucksTestModule],
                declarations: [AdRequestDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    AdRequestService,
                    JhiEventManager
                ]
            }).overrideTemplate(AdRequestDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(AdRequestDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(AdRequestService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new AdRequest('aaa')));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.adRequest).toEqual(jasmine.objectContaining({id: 'aaa'}));
            });
        });
    });

});
