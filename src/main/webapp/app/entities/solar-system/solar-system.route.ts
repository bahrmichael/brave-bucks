import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SolarSystemComponent } from './solar-system.component';
import { SolarSystemPopupComponent } from './solar-system-dialog.component';
import { SolarSystemDeletePopupComponent } from './solar-system-delete-dialog.component';

@Injectable()
export class SolarSystemResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,desc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
      };
    }
}

export const solarSystemRoute: Routes = [
    {
        path: 'solar-system',
        component: SolarSystemComponent,
        resolve: {
            'pagingParams': SolarSystemResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'SolarSystems'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const solarSystemPopupRoute: Routes = [
    {
        path: 'solar-system-new',
        component: SolarSystemPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'SolarSystems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'solar-system/:id/delete',
        component: SolarSystemDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'SolarSystems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'solar-system/:id/edit',
        component: SolarSystemPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'SolarSystems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
];
