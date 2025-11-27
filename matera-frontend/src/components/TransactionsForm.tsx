import React, { useState } from "react";
import type { TransactionItem, TransactionType } from "../types";
import { applyTransactions } from "../api/client";

interface Props {
  accountId?: string;
  onApplied: () => void;
}

export const TransactionsForm: React.FC<Props> = ({
  accountId,
  onApplied
}) => {
  const [items, setItems] = useState<TransactionItem[]>([
    { type: "DEBIT", amount: 0 }
  ]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const updateItem = (index: number, patch: Partial<TransactionItem>) => {
    setItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, ...patch } : item))
    );
  };

  const addItem = () => {
    setItems((prev) => [...prev, { type: "CREDIT", amount: 0 }]);
  };

  const removeItem = (index: number) => {
    setItems((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!accountId) {
      setError("Selecione uma conta primeiro.");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const payload = {
        transactions: items.map((i) => ({
          type: i.type,
          amount: i.amount
        }))
      };
      await applyTransactions(accountId, payload);
      onApplied();
    } catch (err: any) {
      const message =
        err?.response?.data?.message ?? "Erro ao aplicar lançamentos";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-xl shadow p-4">
      <h2 className="text-lg font-semibold mb-3">Lançar débitos/créditos</h2>
      {!accountId && (
        <p className="text-xs text-slate-500 mb-2">
          Selecione uma conta para aplicar lançamentos.
        </p>
      )}
      <form className="space-y-3" onSubmit={handleSubmit}>
        <div className="space-y-2">
          {items.map((item, index) => (
            <div
              key={index}
              className="flex gap-2 items-center border border-slate-200 rounded-lg px-3 py-2"
            >
              <select
                className="rounded-md border border-slate-300 text-sm px-2 py-1"
                value={item.type}
                onChange={(e) =>
                  updateItem(index, {
                    type: e.target.value as TransactionType
                  })
                }
              >
                <option value="DEBIT">DEBIT</option>
                <option value="CREDIT">CREDIT</option>
              </select>
              <input
                type="number"
                step="0.01"
                className="flex-1 rounded-md border border-slate-300 text-sm px-2 py-1"
                value={item.amount}
                onChange={(e) =>
                  updateItem(index, {
                    amount: parseFloat(e.target.value || "0")
                  })
                }
                placeholder="Valor"
              />
              {items.length > 1 && (
                <button
                  type="button"
                  className="text-xs text-red-600 hover:underline"
                  onClick={() => removeItem(index)}
                >
                  Remover
                </button>
              )}
            </div>
          ))}
        </div>
        <button
          type="button"
          onClick={addItem}
          className="text-xs text-blue-600 hover:underline"
        >
          + Adicionar lançamento
        </button>
        {error && (
          <p className="text-sm text-red-600 bg-red-50 px-2 py-1 rounded">
            {error}
          </p>
        )}
        <button
          type="submit"
          disabled={loading || !accountId}
          className="w-full rounded-lg bg-emerald-600 text-white text-sm font-semibold py-2 hover:bg-emerald-700 disabled:opacity-50"
        >
          {loading ? "Aplicando..." : "Aplicar lançamentos"}
        </button>
      </form>
    </div>
  );
};
