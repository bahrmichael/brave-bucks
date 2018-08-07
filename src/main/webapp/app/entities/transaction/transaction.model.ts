import { BaseEntity } from './../../shared';

export const enum TransactionType {
    'SRP',
    'PAYOUT',
    'KILL',
    'PRIZE',
    'RATTING'
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
