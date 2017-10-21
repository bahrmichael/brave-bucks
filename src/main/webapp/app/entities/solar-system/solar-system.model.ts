import { BaseEntity } from './../../shared';

export class SolarSystem implements BaseEntity {
    constructor(
        public id?: string,
        public systemId?: number,
        public systemName?: string,
    ) {
    }
}
