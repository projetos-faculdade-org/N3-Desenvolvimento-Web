# Makefile - Sistema de Oficina Mecânica
# Cada alvo apenas orquestra comandos docker compose / maven.

# Detecta "docker compose" (v2) ou "docker-compose" (v1)
COMPOSE := $(shell docker compose version >/dev/null 2>&1 && echo "docker compose" || echo "docker-compose")

.PHONY: help dev-local-up dev-local-down dev prod-up prod-down logs ps test build clean

help: ## Lista os alvos disponíveis
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-16s\033[0m %s\n", $$1, $$2}'

# ---------- Desenvolvimento (app roda no host) ----------

dev-local-up: ## Sobe a infra (PostgreSQL + Prometheus + Grafana) em Docker
	$(COMPOSE) up -d postgres prometheus grafana

dev-local-down: ## Derruba a infra de desenvolvimento
	$(COMPOSE) down

dev: ## Roda a aplicação localmente (precisa da infra no ar: make dev-local-up)
	./mvnw spring-boot:run

# ---------- Produção (tudo em Docker) ----------

prod-up: ## Sobe tudo em Docker, incluindo o app (build + PostgreSQL + Prometheus + Grafana)
	$(COMPOSE) --profile prod up -d --build

prod-down: ## Derruba todos os containers
	$(COMPOSE) --profile prod down

# ---------- Utilitários ----------

logs: ## Mostra os logs de todos os containers
	$(COMPOSE) --profile prod logs -f

ps: ## Mostra o status dos containers
	$(COMPOSE) --profile prod ps

test: ## Roda os testes da aplicação
	./mvnw test

build: ## Empacota a aplicação (gera o .jar)
	./mvnw clean package

clean: ## Remove containers, volumes e artefatos de build
	$(COMPOSE) --profile prod down -v
	./mvnw clean
