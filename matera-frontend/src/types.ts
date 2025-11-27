export interface AccountSummary {
  accountId: string;
  number: string;
  balance: number;
}

export interface CreateAccountPayload {
  number: string;
  initialBalance: number;
}

export type TransactionType = "DEBIT" | "CREDIT";

export interface TransactionItem {
  type: TransactionType;
  amount: number;
}

export interface TransactionBatchPayload {
  transactions: TransactionItem[];
}

export interface ApiErrorResponse {
  code: string;
  message: string;
}
