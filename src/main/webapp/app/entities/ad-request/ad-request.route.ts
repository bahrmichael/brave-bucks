import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { AdRequestComponent } from './ad-request.component';
import { AdRequestDetailComponent } from './ad-request-detail.component';
import { AdRequestPopupComponent } from './ad-request-dialog.component';
import { AdRequestDeletePopupComponent } from './ad-request-delete-dialog.component';

@Injectable()
export class AdRequestResolvePagingParams implements Resolve<any> {

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

export const adRequestRoute: Routes = [
    {
        path: 'ad-request',
        component: AdRequestComponent,
        resolve: {
            'pagingParams': AdRequestResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AdRequests'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'ad-request/:id',
        component: AdRequestDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AdRequests'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const adRequestPopupRoute: Routes = [
    {
        path: 'ad-request-new',
        component: AdRequestPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AdRequests'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'ad-request/:id/edit',
        component: AdRequestPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AdRequests'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'ad-request/:id/delete',
        component: AdRequestDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'AdRequests'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
