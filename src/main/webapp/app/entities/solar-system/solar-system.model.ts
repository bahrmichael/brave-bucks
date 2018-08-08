import { BaseEntity } from './../../shared';

const enum Region {
    'CATCH',
    'IMPASS'
}

export class SolarSystem implements BaseEntity {
    constructor(
        public id?: string,
        public systemId?: number,
        public systemName?: string,
        public region?: Region,
        public trackPvp?: boolean,
        public trackRatting?: boolean,
    ) {
    }
}
