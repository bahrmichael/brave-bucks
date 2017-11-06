import { BaseEntity } from './../../shared';

const enum AdStatus {
    'REQUESTED',
    'APPROVED',
    'ACTIVE',
    'COMPLETED',
    'DECLINED'
}

export class AdRequest implements BaseEntity {
    constructor(
        public id?: string,
        public requester?: string,
        public service?: string,
        public month?: string,
        public description?: string,
        public link?: string,
        public adStatus?: AdStatus,
    ) {
    }
}
