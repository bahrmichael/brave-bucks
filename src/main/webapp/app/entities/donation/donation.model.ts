import { BaseEntity } from './../../shared';

export class Donation implements BaseEntity {
    constructor(
        public id?: string,
        public donater?: string,
        public month?: string,
        public amount?: number,
    ) {
    }
}
