# 🏦 Desafio Técnico – Bank API
### **Java + Spring Boot + React + Docker**

API RESTful para um sistema bancário capaz de realizar lançamentos de **débito** e **crédito** em contas de clientes, garantindo **consistência**, **concorrência segura** e uma interface simples para testes.

Inclui:

- ⚙️ Backend em **Java 17 + Spring Boot 3**
- 🗄 Persistência com **Spring Data JPA + PostgreSQL**
- 🔐 **Controle de concorrência** com **lock pessimista** (SELECT ... FOR UPDATE) *Optimistic Locking* (`@Version`)
  - Para evitar condições de corrida (race conditions) em cenários de acesso concorrente à mesma conta, o serviço utiliza um `findByIdForUpdate` no repositório, que faz lock pessimista na linha da conta no banco de dados. Assim, apenas uma transação por vez pode modificar o saldo daquela conta, garantindo consistência.
- 🌱 **Seeds automáticos** de contas
- ❗ Tratamento global de erros padronizados
- 🧪 Testes incluindo **cenários concorrentes**
- 🐳 **Docker + docker-compose** (app + banco + frontend)
- 🖥 Frontend em **React + TypeScript + Tailwind**

---

## 📚 Sumário

1. Tecnologias
2. Arquitetura do Backend
3. Seeds
4. Como rodar com Docker
5. Como rodar localmente
6. Endpoints da API
7. Erros
8. Testes

---

## 🔧 Tecnologias

### **Backend**
- Java 17+
- Spring Boot 3
- Maven 3.9+
- Docker e Docker Compose (para rodar via containers)
- Spring Web
- Spring Data JPA
- PostgreSQL (se quiser rodar localmente sem Docker)
- Lombok
- Springdoc OpenAPI (Swagger)

### **Frontend**
- React 18
- TypeScript
- Vite
- TailwindCSS

### **Infra**
- Docker
- Docker Compose

---

## 🧱 Arquitetura do Backend

```
src/main/java/com/desafiotecnico/matera
  ├── MateraApplication.java
  ├── account/
  │     ├── api/AccountController.java
  │     ├── domain/
  │     │    ├── Account.java
  │     │    ├── Transaction.java
  │     │    └── TransactionType.java
  │     ├── dto/
  │     │    ├── BalanceResponse.java
  │     │    ├── CreateAccountRequest.java
  │     │    ├── TransactionBatchRequest.java
  │     │    └── TransactionRequest.java
  │     ├── repository/
  │     │    ├── AccountRepository.java
  │     │    └── TransactionRepository.java
  │     └── service/AccountService.java
  ├── config
  │     ├── DataSeeder.java
  │     ├── CorsConfig.java
  │     ├── OpenApiConfig.java
  └── shared/
        ├── error
        │     ├──ApiErrorResponse.java
        │     ├──ErrorResponse.java
        └── exception/
              ├── ApiExceptionHandler.java
              ├── BusinessException.java
              ├── NotFoundException.java
              └── InsufficientBalanceException.java
```

---

## 🌱 Seeds de Dados

Criados automaticamente ao iniciar a aplicação:

| Conta | Número      | Saldo inicial |
|-------|-------------|---------------|
| Conta 1 | ACC-1001 | 1000.00 + lançamentos |
| Conta 2 | ACC-2001 | 500.00 |
| Conta 3 | ACC-3001 | 0.00 |

---

## 🚀 Como rodar tudo com Docker

### 1️⃣ Subir serviços

- Após descompactar diretório, acessar o diretório descompactado e executar o comando:

```
docker compose up --build
```

- O banco dentro do Docker responde em db:5432 (interno) mas está exposto em localhost:5435 (externo), caso você queira acessar com DBeaver / psql.

### URLs

- Backend → http://localhost:8080
- Swagger → http://localhost:8080/swagger-ui/index.html
- Frontend → http://localhost:8081

---

## ▶️ Rodar Backend localmente (sem Docker)

### Criar banco:

```
CREATE DATABASE bank;
CREATE USER bankuser WITH ENCRYPTED PASSWORD 'bankpass';
GRANT ALL PRIVILEGES ON DATABASE bank TO bankuser;
```

### Configurar `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/bank
spring.datasource.username=bankuser
spring.datasource.password=bankpass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Rodar:

```
./mvnw spring-boot:run
```

---

## 🌐 Endpoints da API

- Para facilitar os testes, no diretório postman-collection contem a coleção (Matera.postman_collection.json) de testes realizados via Postman, basta baixar e importar diretamente no aplicativo

### Criar conta
**POST** `/api/accounts`

```
{
  "number": "12345-0",
  "initialBalance": 1000.00
}
```

---

### Listar contas
**GET** `/api/accounts`

---

### Lançamentos em lote
**POST** `/api/accounts/{id}/transactions`

```
{
  "transactions": [
    { "type": "DEBIT", "amount": 100.00 },
    { "type": "CREDIT", "amount": 50.00 }
  ]
}
```

---

### Buscar saldo
**GET** `/api/accounts/{id}/balance`

---

## ❗ Erros

### Exemplos

Saldo insuficiente:

```
{
  "code": "INSUFFICIENT_BALANCE",
  "message": "Saldo insuficiente."
}
```

Conta não encontrada:

```
{
  "code": "NOT_FOUND",
  "message": "Conta não encontrada"
}
```

Concorrência:

```
{
  "code": "CONCURRENT_MODIFICATION",
  "message": "A conta foi modificada por outra transação."
}
```

---

## 🧪 Testes

```
./mvnw test
```

Inclui testes de:

- Débito / Crédito
- Saldo insuficiente
- Concorrência  

