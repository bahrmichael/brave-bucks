import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Http} from "@angular/http";
import {Principal} from "../../shared/auth/principal.service";
import {StateStorageService} from "../../shared/auth/state-storage.service";
import {JhiEventManager} from "ng-jhipster";
import {LocalStorageService, SessionStorageService} from "ng2-webstorage";

@Component({
               selector: 'jhi-sso', templateUrl: './sso.component.html'
           })
export class SsoComponent implements OnInit {

    private authenticationError: boolean;

    constructor(private route: ActivatedRoute, private http: Http, private principal: Principal, private stateStorageService: StateStorageService,
                private eventManager: JhiEventManager, private router: Router,
                private $localStorage: LocalStorageService,
                private $sessionStorage: SessionStorageService) {
    }

    ngOnInit() {
        this.route.queryParams.subscribe((params) => {
            const code = params['code'];
            const state = params['state'];
            return this.http.get('api/authenticate/sso?code=' + code + '&state=' + state).map(authenticateSuccess.bind(this))
                       .subscribe((jwt) => {
                           this.principal.identity(true).then((account) => {
                               this.eventManager.broadcast({
                                                               name: 'authenticationSuccess', content: 'Sending Authentication Success'
                                                           });

                               // // previousState was set in the authExpiredInterceptor before being redirected to
                               // login modal. // since login is succesful, go to stored previousState and clear
                               // previousState
                               const redirect = this.stateStorageService.getUrl();
                               if (redirect) {
                                   this.stateStorageService.storeUrl(null);
                                   this.router.navigate([redirect]);
                               } else {
                                   this.router.navigate(["/"]);
                               }
                           }, () => {
                               this.authenticationError = true;
                           });
                       });

            function authenticateSuccess(resp) {
                const bearerToken = resp.headers.get('Authorization');
                if (bearerToken && bearerToken.slice(0, 7) === 'Bearer ') {
                    const jwt = bearerToken.slice(7, bearerToken.length);
                    this.storeAuthenticationToken(jwt, true);
                    return jwt;
                }
            }
        });
    }

    storeAuthenticationToken(jwt, rememberMe) {
        if (rememberMe) {
            this.$localStorage.store('authenticationToken', jwt);
        } else {
            this.$sessionStorage.store('authenticationToken', jwt);
        }
    }
}
