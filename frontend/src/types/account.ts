export type AccountType = "SAVINGS" | "CURRENT";

export type AccountStatus = "ACTIVE" | "BLOCKED" | "CLOSED";

export interface Account {
  id: number;
  accountNumber: string;
  accountType: AccountType;
  balance: number;
  currency: string;
  status: AccountStatus;
}
