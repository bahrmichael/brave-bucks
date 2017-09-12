export class Account {
    constructor(
        public activated: boolean,
        public authorities: string[],
        public login: string,
    ) { }
}
