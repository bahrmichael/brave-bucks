import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils } from 'ng-jhipster';

import { Payout } from './payout.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class PayoutService {

    private resourceUrl = 'api/payouts';

    constructor(private http: Http, private dateUtils: JhiDateUtils) { }

    create(payout: Payout): Observable<Payout> {
        const copy = this.convert(payout);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    update(payout: Payout): Observable<Payout> {
        const copy = this.convert(payout);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    find(id: string): Observable<Payout> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: string): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            this.convertItemFromServer(jsonResponse[i]);
        }
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convertItemFromServer(entity: any) {
        entity.lastUpdated = this.dateUtils
            .convertDateTimeFromServer(entity.lastUpdated);
    }

    private convert(payout: Payout): Payout {
        const copy: Payout = Object.assign({}, payout);

        copy.lastUpdated = this.dateUtils.toDate(payout.lastUpdated);
        return copy;
    }

    getTotalValue() {
        return this.http.get(this.resourceUrl + '/total');
    }
}
