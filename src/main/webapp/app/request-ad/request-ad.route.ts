import { Route } from '@angular/router';

import { RequestAdComponent } from './';

export const RequestAd_ROUTE: Route = {
    path: 'request-ad',
    component: RequestAdComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'Request Your Ad'
    }
};
