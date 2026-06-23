# Sistema de Gerenciamento de Oficina Mecânica

# Resumo rápido
### Executar projeto
- Necessário docker instalado na máquina

```bash
# Tudo em Docker (app + PostgreSQL + Prometheus + Grafana)
make prod-up

# Ou: infra em Docker + app no host (desenvolvimento)
make dev-local-up
make dev
```

| Recurso | URL |
|---------|-----|
| Aplicação | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Grafana | http://localhost:3000 (`admin` / `admin`) |
| Prometheus | http://localhost:9090 |

Stack: Spring Boot 3.3 · Java 21 · PostgreSQL 16 · Spring Data JPA · SpringDoc/Swagger ·
Actuator + Micrometer/Prometheus · Grafana · Docker Compose.


--- 
API REST (Spring Boot) + Front-end, com PostgreSQL, observabilidade (Actuator + Prometheus + Grafana)
e orquestração via Docker Compose.

---

## 1. Identificação do Time

| Nome Completo |
|---------------|
| `Gustavo Henrique Sora` | 
| `João Emanuel Neri` | 
| `Guilherme Worel` | 
| `Leon Fagner` |
| `Vinicius Pedroso Schoeffel` | 

---

## 2. Título do Trabalho

**Sistema de Gerenciamento de Oficina Mecânica**

---

## 3. Descrição do Sistema

### O que o sistema faz

Permite que uma oficina mecânica gerencie seus **clientes** e as **ordens de serviço** associadas a eles.
O problema de negócio resolvido é controlar quais serviços foram solicitados por cada cliente, o status de
cada reparo e o valor cobrado.

### Relacionamento entre as entidades

- **Cliente** (`/clientes`): nome, CPF, telefone e e-mail.
- **OrdemServico** (`/ordens`): veículo, problema relatado, status (`ABERTO`, `EM_ANDAMENTO`, `FINALIZADO`) e valor.

Cada ordem pertence a **um cliente** — relacionamento **`Cliente 1 : N OrdemServico`** (`@ManyToOne`).
Excluir/editar clientes e ordens é feito pelas telas ou pela API.

### Roteiro de navegação (fluxo principal)

1. Acesse **http://localhost:8080** — página inicial com links para as telas e para o Swagger.
2. **Tela de Clientes** (`/clientes.html`):
   - Preencha nome, CPF, telefone e (opcional) e-mail e clique em **Salvar** para cadastrar.
   - A tabela lista todos os clientes; use **Editar** ou **Excluir**.
3. **Tela de Ordens de Serviço** (`/ordens.html`):
   - Selecione um cliente, informe veículo, problema, status e valor, e clique em **Salvar**.
   - A tabela exibe as ordens com destaque visual por status.
   - Use o **filtro por status** para ver apenas ordens em determinado estado.
   - Use **Editar** / **Excluir** para gerenciar cada ordem.

---

## 4. Guia Passo a Passo de Execução

### Pré-requisitos

| Ferramenta | Versão |
|------------|--------|
| Docker + Docker Compose | recente (v2) |
| Make | opcional, mas recomendado |
| Java JDK | 21 *(somente para o modo de desenvolvimento local)* |

### Modo recomendado — tudo em Docker (1 comando)

```bash
cd oficina-mecanica
make prod-up
```

Isso faz o build da aplicação e sobe **app + PostgreSQL + Prometheus + Grafana**. O banco já vem com
**dados de exemplo** (3 clientes e 3 ordens). Acompanhe a inicialização com `make ps` e `make logs`.

Para derrubar tudo: `make prod-down`.

> **Sem `make`?** (ex.: Windows) Use os comandos equivalentes:
> ```bash
> docker compose --profile prod up -d --build   # subir tudo
> docker compose --profile prod down            # derrubar
> ```

### Modo desenvolvimento — app no host, infra em Docker

```bash
cd oficina-mecanica
make dev-local-up   # sobe PostgreSQL + Prometheus + Grafana em Docker
make dev            # roda a aplicação localmente (./mvnw spring-boot:run)
```

### URLs de acesso

| Recurso | URL |
|---------|-----|
| Tela inicial | http://localhost:8080 |
| Tela de Clientes | http://localhost:8080/clientes.html |
| Tela de Ordens de Serviço | http://localhost:8080/ordens.html |
| **Swagger UI** (testar a API) | http://localhost:8080/swagger-ui.html |
| Actuator (saúde) | http://localhost:8080/actuator/health |
| Métricas Prometheus | http://localhost:8080/actuator/prometheus |
| Prometheus | http://localhost:9090 |
| **Grafana** (dashboard) | http://localhost:3000 |

### Credenciais padrão

| Sistema | Usuário | Senha |
|---------|---------|-------|
| Grafana | `admin` | `admin` |
| PostgreSQL | `oficina` | `oficina` (banco `oficina`, porta `5432`) |

> O sistema **não possui login** — as telas e a API são de acesso aberto. As credenciais acima são apenas
> de infraestrutura (Grafana e banco).

---

## 5. Testando a API pelo Swagger

Abra http://localhost:8080/swagger-ui.html. Todos os endpoints de **Clientes** e **Ordens de Serviço**
aparecem com os verbos (GET/POST/PUT/DELETE) e os modelos de dados.

Exemplo de corpo para `POST /ordens` (note que o cliente é referenciado por **`clienteId`**):

```json
{
  "clienteId": 1,
  "veiculo": "Honda Civic 2020",
  "problema": "Troca de óleo",
  "status": "ABERTO",
  "valor": 150.00
}
```

Status válidos: `ABERTO`, `EM_ANDAMENTO`, `FINALIZADO`.

---

## 6. Observabilidade (Grafana)

O Grafana já sobe **com o datasource Prometheus e o dashboard provisionados automaticamente** — não é
preciso configurar nada manualmente.

1. Acesse http://localhost:3000 e faça login (`admin` / `admin`).
2. Abra o dashboard **"Oficina Mecânica - Observabilidade"** (menu **Dashboards**).
3. Os painéis exigidos já estão prontos:
   - **Uso de CPU** (`process_cpu_usage`)
   - **Memória JVM (heap)** (`jvm_memory_used_bytes`)
   - **Tempo médio de resposta HTTP**
   - **Conexões do banco de dados** (HikariCP) e **Saúde da aplicação** (`up`)
   - **Taxa de requisições HTTP**

---

## 7. Arquitetura

- **Back-end:** Spring Boot 3.3 / Java 21 — API REST em 3 camadas (`Controller` → `Service` → `Repository`),
  com `Model` (entidades JPA), `DTO` e tratamento de exceção.
- **Front-end:** HTML + CSS + JavaScript (fetch), consumindo a API REST (JSON).
- **Banco:** PostgreSQL 16 (container, com volume persistente).
- **Documentação da API:** SpringDoc OpenAPI (Swagger UI).
- **Observabilidade:** Spring Boot Actuator + Micrometer (Prometheus) + Grafana (dashboard provisionado).
- **Infra:** Docker Compose (app, postgres, prometheus, grafana) + Makefile.

```
oficina-mecanica/
├── src/main/java/com/oficina/
│   ├── controller/   # REST Controllers
│   ├── service/      # Regras de negócio
│   ├── repository/   # Spring Data JPA
│   ├── model/        # Entidades (Cliente, OrdemServico, StatusOrdem)
│   ├── dto/          # DTOs de entrada/saída
│   ├── exception/    # ResourceNotFoundException
│   └── config/       # DataSeeder (dados de exemplo)
├── src/main/resources/static/   # Front-end (index, clientes, ordens)
├── grafana/          # Provisioning de datasource + dashboard
├── Dockerfile        # Imagem da aplicação (multi-stage)
├── docker-compose.yml
├── prometheus.yml
└── Makefile
```
