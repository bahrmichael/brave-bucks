import { BaseEntity } from './../../shared';

const enum TransactionType {
    'SRP',
    'PAYOUT',
    'KILL'
}

export class Transaction implements BaseEntity {
    constructor(
        public id?: string,
        public user?: string,
        public instant?: any,
        public amount?: number,
        public type?: TransactionType,
    ) {
    }
}
