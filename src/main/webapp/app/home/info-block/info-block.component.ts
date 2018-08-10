import { Component, OnInit } from '@angular/core';
import {ConfigService, Principal} from "../../shared";
import {Http} from "@angular/http";
import {SolarSystem} from "../../entities/solar-system/solar-system.model";

@Component({
  selector: 'jhi-info-block',
  templateUrl: './info-block.component.html',
  styles: []
})
export class InfoBlockComponent implements OnInit {

    systemsCatch: SolarSystem[];
    systemsImpass: SolarSystem[];
    walletUrl: string;
    characterNames: string[];

    constructor(private configService: ConfigService, private principal: Principal, private http: Http) {
    }

    ngOnInit() {
        this.principal.identity().then((account) => {
            this.configService.getWalletUrl().subscribe((data) => this.walletUrl = data + "-" + account.id);
        });

        this.http.get('/api/solar-systems/region/CATCH').subscribe((data) => {
            this.systemsCatch = data.json();
        });
        this.http.get('/api/solar-systems/region/IMPASS').subscribe((data) => {
            this.systemsImpass = data.json();
        });
        this.http.get('/api/characters').subscribe((data) => {
            this.characterNames = data.json();
        });
    }

    revokeCharacter(characterName: string) {
        this.http.delete('/api/characters/' + characterName).subscribe((data) => {
            this.http.get('/api/characters').subscribe((charNames) => {
                this.characterNames = charNames.json();
            });
        });
    }

    getDotlanLink(region: string, isPvp: boolean, isRatting: boolean) {
        const systemNames = [];
        if (region === 'Catch') {
            this.systemsCatch.forEach((s) => {
                if (isPvp && s.trackPvp || isRatting && s.trackRatting) {
                    systemNames.push(s.systemName);
                }
            });
        } else {
            this.systemsImpass.forEach((s) => {
                if (isPvp && s.trackPvp || isRatting && s.trackRatting) {
                    systemNames.push(s.systemName);
                }
            });
        }
        return "http://evemaps.dotlan.net/map/" + region + '/' + systemNames.join();
    }

}
