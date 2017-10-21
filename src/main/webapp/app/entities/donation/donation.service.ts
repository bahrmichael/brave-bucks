import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Donation } from './donation.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class DonationService {

    private resourceUrl = 'api/donations';

    constructor(private http: Http) { }

    create(donation: Donation): Observable<Donation> {
        const copy = this.convert(donation);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(donation: Donation): Observable<Donation> {
        const copy = this.convert(donation);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: string): Observable<Donation> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
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
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convert(donation: Donation): Donation {
        const copy: Donation = Object.assign({}, donation);
        return copy;
    }
}
