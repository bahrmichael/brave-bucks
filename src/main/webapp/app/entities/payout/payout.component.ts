import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs/Rx';
import {JhiAlertService, JhiEventManager, JhiPaginationUtil, JhiParseLinks} from 'ng-jhipster';

import {Payout} from './payout.model';
import {PayoutService} from './payout.service';
import {ITEMS_PER_PAGE, Principal, ResponseWrapper} from '../../shared';
import {PaginationConfig} from '../../blocks/config/uib-pagination.config';
import {ClipboardService} from "./clipboard.service";

@Component({
    selector: 'jhi-payout',
    templateUrl: './payout.component.html'
})
export class PayoutComponent implements OnInit, OnDestroy {

currentAccount: any;
    payouts: Payout[];
    error: any;
    success: any;
    eventSubscriber: Subscription;
    routeData: any;
    links: any;
    totalItems: any;
    queryCount: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    previousPage: any;
    reverse: any;
    allAccounts: number;
    public showCopiedPriceFor: string = null;
    public showCopiedNameFor: string = null;

    constructor(
        private payoutService: PayoutService,
        private parseLinks: JhiParseLinks,
        private alertService: JhiAlertService,
        private principal: Principal,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private eventManager: JhiEventManager,
        private paginationUtil: JhiPaginationUtil,
        private paginationConfig: PaginationConfig,
        private clipboard: ClipboardService
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe((data) => {
            this.page = data['pagingParams'].page;
            this.previousPage = data['pagingParams'].page;
            this.reverse = data['pagingParams'].ascending;
            this.predicate = data['pagingParams'].predicate;
        });
    }

    copyName(name: string, id: string) {
        this.clipboard.copy(name + '');

        this.showCopiedNameFor = id;
        setTimeout(function() {
            this.showCopiedNameFor = null;
        }.bind(this), 3000);
    }

    copyPrice(price: number, id: string) {
        this.clipboard.copy(price + '');

        this.showCopiedPriceFor = id;
        setTimeout(function() {
            this.showCopiedPriceFor = null;
        }.bind(this), 3000);

        const payout = this.payouts.find(p => p.id === id);

        if (payout.status.toString() === 'REQUESTED') {
            this.payoutService.markPaid(id).subscribe((data) => {
                this.payouts.forEach((p) => {
                    if (p.id === id) {
                        p.status = 1;
                    }
                });
            });
        }
    }

    loadAll() {
        this.payoutService.query({
            page: this.page - 1,
            size: this.itemsPerPage,
            sort: this.sort()}).subscribe(
            (res: ResponseWrapper) => this.onSuccess(res.json, res.headers),
            (res: ResponseWrapper) => this.onError(res.json)
        );
        this.payoutService.getTotalValue().subscribe(
            (data) => this.allAccounts = +data.text()
        );
    }
    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }
    transition() {
        this.router.navigate(['/payout'], {queryParams:
            {
                page: this.page,
                size: this.itemsPerPage,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        });
        this.loadAll();
    }

    clear() {
        this.page = 0;
        this.router.navigate(['/payout', {
            page: this.page,
            sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
        }]);
        this.loadAll();
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInPayouts();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Payout) {
        return item.id;
    }
    registerChangeInPayouts() {
        this.eventSubscriber = this.eventManager.subscribe('payoutListModification', (response) => this.loadAll());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private onSuccess(data, headers) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        this.queryCount = this.totalItems;
        // this.page = pagingParams.page;
        this.payouts = data;
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
