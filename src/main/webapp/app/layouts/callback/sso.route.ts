import {Route} from '@angular/router';
import {SsoComponent} from "./sso.component";

export const ssoRoute: Route = {
    path: 'callback',
    component: SsoComponent
};
