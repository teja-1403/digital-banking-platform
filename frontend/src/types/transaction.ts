export type TransactionType = "DEBIT" | "CREDIT" | "TRANSFER";

export type TransactionStatus = "PENDING" | "COMPLETED" | "FAILED";

export interface TransferRequest {
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  currency: string;
  description?: string;
}

export interface TransactionResponse {
  id: number;
  transactionReference: string;
  idempotencyKey: string;
  type: TransactionType;
  status: TransactionStatus;
  sourceAccountId: number;
  destinationAccountId: number;
  amount: number;
  currency: string;
  description?: string;
  createdAt: string;
  completedAt?: string;
}
