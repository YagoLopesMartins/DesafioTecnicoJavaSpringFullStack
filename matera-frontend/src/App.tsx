import React, { useEffect, useState } from "react";
import type { AccountSummary } from "./types";
import { fetchAccounts } from "./api/client";
import { AccountList } from "./components/AccountList";
import { AccountForm } from "./components/AccountForm";
import { TransactionsForm } from "./components/TransactionsForm";
import { BalanceCard } from "./components/BalanceCard";

const API_BASE =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

const App: React.FC = () => {
  const [accounts, setAccounts] = useState<AccountSummary[]>([]);
  const [selectedId, setSelectedId] = useState<string | undefined>();
  const [loadingAccounts, setLoadingAccounts] = useState(false);

  const loadAccounts = async () => {
    setLoadingAccounts(true);
    try {
      const data = await fetchAccounts();
      setAccounts(data);
      if (!selectedId && data.length > 0) {
        setSelectedId(data[0].accountId);
      }
    } catch (err) {
      console.error("Erro ao carregar contas", err);
    } finally {
      setLoadingAccounts(false);
    }
  };

  useEffect(() => {
    void loadAccounts();
  }, []);

  const selectedAccount = accounts.find((a) => a.accountId === selectedId);

  return (
    <div className="min-h-screen">
      <header className="bg-slate-900 text-white py-4 shadow">
        <div className="max-w-5xl mx-auto px-4 flex justify-between items-center">
          <div>
            <h1 className="text-xl font-semibold">Bank UI – Desafio Matera</h1>
            <p className="text-xs text-slate-300">
              Lançamentos bancários com Java + Spring Boot + React
            </p>
          </div>
          <div className="text-xs text-slate-300">
            API: <span className="font-mono">{API_BASE}</span>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-6 space-y-6">
        <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="md:col-span-2 space-y-4">
            <div className="flex justify-between items-center">
              <h2 className="text-base font-semibold">Contas</h2>
              <button
                onClick={loadAccounts}
                className="text-xs text-blue-600 hover:underline"
              >
                {loadingAccounts ? "Atualizando..." : "Atualizar lista"}
              </button>
            </div>
            <AccountList
              accounts={accounts}
              selectedId={selectedId}
              onSelect={setSelectedId}
            />
          </div>
          <div>
            <AccountForm onCreated={loadAccounts} />
          </div>
        </section>

        <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="md:col-span-2 space-y-4">
            <BalanceCard account={selectedAccount} />
            <TransactionsForm accountId={selectedId} onApplied={loadAccounts} />
          </div>
          <div className="bg-white rounded-xl shadow p-4 text-sm text-slate-600">
            <h2 className="text-base font-semibold mb-2">Como usar</h2>
            <ol className="list-decimal list-inside space-y-1">
              <li>Use o formulário para criar uma nova conta, se quiser.</li>
              <li>Selecione uma conta na lista à esquerda.</li>
              <li>Veja o saldo atual no card de saldo.</li>
              <li>
                Use o formulário de lançamentos para adicionar débitos e
                créditos em lote.
              </li>
              <li>
                Clique em &quot;Atualizar lista&quot; para ver o saldo
                atualizado.
              </li>
            </ol>
          </div>
        </section>
      </main>
    </div>
  );
};

export default App;
