import React, { useState } from "react";
import { createAccount } from "../api/client";

interface Props {
  onCreated: () => void;
}

export const AccountForm: React.FC<Props> = ({ onCreated }) => {
  const [number, setNumber] = useState("");
  const [initialBalance, setInitialBalance] = useState("0");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const payload = {
        number,
        initialBalance: parseFloat(initialBalance || "0")
      };
      await createAccount(payload);
      setNumber("");
      setInitialBalance("0");
      onCreated();
    } catch (err: any) {
      const message =
        err?.response?.data?.message ?? "Erro ao criar conta";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white rounded-xl shadow p-4 h-full">
      <h2 className="text-lg font-semibold mb-3">Criar nova conta</h2>
      <form className="space-y-3" onSubmit={handleSubmit}>
        <div className="space-y-1">
          <label className="block text-sm font-medium text-slate-700">
            Número da conta
          </label>
          <input
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={number}
            onChange={(e) => setNumber(e.target.value)}
            placeholder="Ex: 12345-0"
            required
          />
        </div>
        <div className="space-y-1">
          <label className="block text-sm font-medium text-slate-700">
            Saldo inicial
          </label>
          <input
            type="number"
            step="0.01"
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={initialBalance}
            onChange={(e) => setInitialBalance(e.target.value)}
          />
        </div>
        {error && (
          <p className="text-sm text-red-600 bg-red-50 px-2 py-1 rounded">
            {error}
          </p>
        )}
        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-lg bg-blue-600 text-white text-sm font-semibold py-2 hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? "Criando..." : "Criar conta"}
        </button>
      </form>
    </div>
  );
};
