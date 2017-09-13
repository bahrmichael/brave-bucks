import { Injectable } from '@angular/core';
import { Http } from "@angular/http";
import { Observable } from 'rxjs/Rx';

@Injectable()
export class ConfigService {

    constructor(private http: Http) {}

    getSsoUrl(): Observable<string> {
        return this.http.get('api/config/ssourl').map((res: any) => {
            return res.text();
        });
    }

}
