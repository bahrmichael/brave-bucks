import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { DonationComponent } from './donation.component';
import { DonationDetailComponent } from './donation-detail.component';
import { DonationPopupComponent } from './donation-dialog.component';
import { DonationDeletePopupComponent } from './donation-delete-dialog.component';

@Injectable()
export class DonationResolvePagingParams implements Resolve<any> {

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

export const donationRoute: Routes = [
    {
        path: 'donation',
        component: DonationComponent,
        resolve: {
            'pagingParams': DonationResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Donations'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'donation/:id',
        component: DonationDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Donations'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const donationPopupRoute: Routes = [
    {
        path: 'donation-new',
        component: DonationPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Donations'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'donation/:id/edit',
        component: DonationPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Donations'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'donation/:id/delete',
        component: DonationDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Donations'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
