/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { ThebuybackTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { SolarSystemDetailComponent } from '../../../../../../main/webapp/app/entities/solar-system/solar-system-detail.component';
import { SolarSystemService } from '../../../../../../main/webapp/app/entities/solar-system/solar-system.service';
import { SolarSystem } from '../../../../../../main/webapp/app/entities/solar-system/solar-system.model';

describe('Component Tests', () => {

    describe('SolarSystem Management Detail Component', () => {
        let comp: SolarSystemDetailComponent;
        let fixture: ComponentFixture<SolarSystemDetailComponent>;
        let service: SolarSystemService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [ThebuybackTestModule],
                declarations: [SolarSystemDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    SolarSystemService,
                    JhiEventManager
                ]
            }).overrideTemplate(SolarSystemDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SolarSystemDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SolarSystemService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new SolarSystem('aaa')));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.solarSystem).toEqual(jasmine.objectContaining({id: 'aaa'}));
            });
        });
    });

});
