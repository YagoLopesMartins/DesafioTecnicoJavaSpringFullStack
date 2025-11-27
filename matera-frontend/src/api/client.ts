import axios from "axios";
import type {
  AccountSummary,
  CreateAccountPayload,
  TransactionBatchPayload
} from "../types";

const API_BASE =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

export const api = axios.create({
  baseURL: API_BASE,
  headers: {
    "Content-Type": "application/json"
  }
});

export async function fetchAccounts(): Promise<AccountSummary[]> {
  const { data } = await api.get<AccountSummary[]>("/accounts");
  return data;
}

export async function createAccount(payload: CreateAccountPayload): Promise<void> {
  await api.post("/accounts", payload);
}

export async function applyTransactions(
  accountId: string,
  payload: TransactionBatchPayload
): Promise<void> {
  await api.post(`/accounts/${accountId}/transactions`, payload);
}
