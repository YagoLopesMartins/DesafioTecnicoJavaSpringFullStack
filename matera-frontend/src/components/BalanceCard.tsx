import React from "react";
import type { AccountSummary } from "../types";

interface Props {
  account?: AccountSummary;
}

export const BalanceCard: React.FC<Props> = ({ account }) => {
  if (!account) {
    return (
      <div className="bg-white rounded-xl shadow p-4">
        <p className="text-sm text-slate-500">
          Selecione uma conta para visualizar o saldo.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow p-4">
      <h2 className="text-lg font-semibold mb-2">
        Saldo da conta {account.number}
      </h2>
      <p className="text-3xl font-bold text-emerald-600 mb-1">
        R$ {account.balance.toFixed(2)}
      </p>
      <p className="text-xs text-slate-500">ID: {account.accountId}</p>
    </div>
  );
};
