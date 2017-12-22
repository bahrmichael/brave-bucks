import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { PayoutComponent } from './payout.component';
import { PayoutDetailComponent } from './payout-detail.component';
import { PayoutPopupComponent } from './payout-dialog.component';
import { PayoutDeletePopupComponent } from './payout-delete-dialog.component';

@Injectable()
export class PayoutResolvePagingParams implements Resolve<any> {

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

export const payoutRoute: Routes = [
    {
        path: 'payout',
        component: PayoutComponent,
        resolve: {
            'pagingParams': PayoutResolvePagingParams
        },
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'Payouts'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'payout/:id',
        component: PayoutDetailComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'Payouts'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const payoutPopupRoute: Routes = [
    {
        path: 'payout-new',
        component: PayoutPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'Payouts'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'payout/:id/edit',
        component: PayoutPopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'Payouts'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'payout/:id/delete',
        component: PayoutDeletePopupComponent,
        data: {
            authorities: ['ROLE_MANAGER'],
            pageTitle: 'Payouts'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
