import { BaseEntity } from './../../shared';

const enum PayoutStatus {
    'REQUESTED',
    'PAID',
    'CANCELLED'
}

export class Payout implements BaseEntity {
    constructor(
        public id?: string,
        public user?: string,
        public amount?: number,
        public lastUpdated?: any,
        public lastModifiedBy?: string,
        public status?: PayoutStatus,
        public details?: string,
    ) {
    }
}
