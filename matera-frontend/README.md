# 💳 Frontend – Bank API

Interface web em **React + TypeScript + Vite + TailwindCSS** para consumir a Bank API desenvolvida em Spring Boot.

O objetivo é permitir que a pessoa usuária:

- visualize as contas disponíveis
- consulte saldos
- simule lançamentos de **débito** e **crédito** em uma conta

---

## 🧰 Tecnologias

- **React 18**
- **TypeScript**
- **Vite**
- **TailwindCSS**
- Integração com backend em **Spring Boot** via HTTP

---

## ✅ Pré-requisitos

Para rodar localmente:

- Node.js 18+ (ou 20+)
- npm ou yarn
- Backend da Bank API rodando (ex.: em `http://localhost:8080`)

Para rodar via Docker:

- Docker
- Docker Compose
- Backend e banco já estão orquestrados pelo `docker-compose.yml` na raiz do projeto

---

## ⚙️ Variáveis de ambiente

O frontend utiliza uma variável para apontar para a API:

- `VITE_API_BASE_URL`

### Exemplos de uso

- Ambiente local (sem Docker):

```bash
  VITE_API_BASE_URL=http://localhost:8080
```

### ▶️ Rodando localmente (sem Docker):

#### Na pasta matera-frontend:

- instalar dependências e rodar servidor de desenvolvimento
```bash
    npm install
    npm run dev
```

- Por padrão, o Vite sobe em: http://localhost:5173/