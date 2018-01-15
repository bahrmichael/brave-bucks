import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils } from 'ng-jhipster';

import {Transaction, TransactionType} from './transaction.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class TransactionService {

    private resourceUrl = 'api/transactions';

    constructor(private http: Http, private dateUtils: JhiDateUtils) { }

    create(transaction: Transaction): Observable<Transaction> {
        const copy = this.convert(transaction);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    addPrize(recipient: string, amount: number) {
        const transaction = new Transaction();
        transaction.type = TransactionType.PRIZE;
        transaction.amount = amount;
        transaction.user = recipient;
        return this.http.post(this.resourceUrl + '/prize/', transaction);
    }

    update(transaction: Transaction): Observable<Transaction> {
        const copy = this.convert(transaction);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    find(id: string): Observable<Transaction> {
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
        entity.instant = this.dateUtils
            .convertDateTimeFromServer(entity.instant);
    }

    private convert(transaction: Transaction): Transaction {
        const copy: Transaction = Object.assign({}, transaction);

        copy.instant = this.dateUtils.toDate(transaction.instant);
        return copy;
    }
}
