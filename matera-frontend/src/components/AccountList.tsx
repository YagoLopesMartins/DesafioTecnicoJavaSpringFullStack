import React from "react";
import type { AccountSummary } from "../types";

interface Props {
  accounts: AccountSummary[];
  selectedId?: string;
  onSelect: (accountId: string) => void;
}

export const AccountList: React.FC<Props> = ({
  accounts,
  selectedId,
  onSelect
}) => {
  return (
    <div className="bg-white rounded-xl shadow p-4">
      <h2 className="text-lg font-semibold mb-3">Contas disponíveis</h2>
      {accounts.length === 0 ? (
        <p className="text-sm text-slate-500">
          Nenhuma conta encontrada. Crie uma nova conta ao lado.
        </p>
      ) : (
        <ul className="space-y-2 max-h-64 overflow-y-auto">
          {accounts.map((acc) => (
            <li
              key={acc.accountId}
              className={`flex items-center justify-between px-3 py-2 rounded-lg border cursor-pointer ${
                acc.accountId === selectedId
                  ? "border-blue-500 bg-blue-50"
                  : "border-slate-200 hover:border-blue-300"
              }`}
              onClick={() => onSelect(acc.accountId)}
            >
              <div>
                <div className="font-medium">{acc.number}</div>
                <div className="text-xs text-slate-500">
                  ID: {acc.accountId}
                </div>
              </div>
              <div className="text-sm font-semibold text-emerald-600">
                Saldo: R$ {acc.balance.toFixed(2)}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};
