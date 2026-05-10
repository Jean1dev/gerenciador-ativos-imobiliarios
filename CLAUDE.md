# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A real estate / investment asset manager (gerenciador de ativos imobiliários) consisting of three modules:

- **dominio** — Java 21 library: core domain logic (Clean Architecture)
- **infra** — Java 21 Spring Boot 3.4.3 app: HTTP API, MongoDB persistence, Redis cache
- **agent** — TypeScript/Node.js 20: AI-powered fundamentalist analysis pipeline using LangChain + Google Gemini

## Build & Run Commands

### Backend (Java/Gradle)

```bash
./gradlew clean build           # Build all modules
./gradlew :infra:bootRun        # Start Spring Boot API on port 8080
./gradlew test                  # Run all tests
./gradlew :infra:test           # Run infra integration tests (requires Docker for Testcontainers)
./gradlew :infra:test --tests "br.com.carteira.infra.SomeTest"  # Run a single test class
```

### Infrastructure dependencies (MongoDB + Redis)

```bash
docker-compose up               # Start MongoDB and Redis locally
```

### Agent (TypeScript)

```bash
cd agent
npm install
npm run dev                     # Run agent pipeline with tsx (development)
npm run build                   # Compile TypeScript
npm start                       # Run compiled output
npm run upload-relatorio        # POST generated report to Java API
npm run lint                    # ESLint
```

Agent required environment variables:

```
GOOGLE_API_KEY     # Google Gemini
TAVILY_API_KEY     # Tavily web search
API_BASE_URL       # Defaults to http://localhost:8080
```

## Architecture

### Layering (Clean Architecture)

```
dominio/          ← pure domain: entities, use cases, gateway interfaces
infra/            ← Spring Boot: implements gateways, exposes REST, MongoDB/Redis
agent/            ← TypeScript pipeline: calls infra API, runs LLM analysis, uploads results
```

The **dominio** module has zero Spring dependencies. Infra wires everything together by implementing the domain's
gateway interfaces.

### Domain Model (dominio)

- `Ativo` — abstract base asset; concrete types: `AcaoNacional`, `AcaoInternacional`, `Crypto`, `RendaFixa`,
  `AtivoComTicker` (FIIs/REITs)
- `Carteira` — user portfolio; holds a list of `Ativo`, computes allocation/diversification
- `Meta` — predefined portfolio strategy (CONSERVADOR, MODERADO, ARROJADO, DINAMICO, SOFISTICADO)
- `Cotacao` — price quote attached to an asset
- Use cases live in `dominio/src/main/java/br/com/carteira/dominio/*/usecase/`
- Gateways (interfaces): `CarteiraGateway`, `AtivosComTickerGateway`, `CotacaoGateway`
- `DomainEventsEmiter` handles async domain events (quote updates, portfolio consolidation)

### Infrastructure Layer (infra)

Key Spring packages under `br.com.carteira.infra`:

| Package                     | Responsibility                                               |
|-----------------------------|--------------------------------------------------------------|
| `ativo`                     | Asset CRUD, aporte (deposit), ticker suggestions             |
| `carteira`                  | Portfolio creation, asset listing, meta strategies           |
| `cotacao`                   | Price quote ingestion from Alpha Vantage / CoinMarketCap     |
| `avaliacao-fundamentalista` | Store/retrieve AI-generated evaluations                      |
| `marketplace`               | Browse available assets for purchase                         |
| `criterio`                  | Fundamentalist criteria (per asset type: FII, ACAO_NACIONAL) |
| `usuario`                   | User management                                              |

Controllers call domain use cases through gateway implementations backed by Spring Data MongoDB. Redis is used for quote
caching (`JedisConfig`).

The `-parameters` compiler flag is set in `infra/build.gradle` — required for Spring Data MongoDB mapping.

### Agent Pipeline (agent/src)

```
config/env.ts        → validate env vars
api/client.ts        → fetch asset listings + criteria from Java API
research/            → Tavily web searches on financial sites (fundamentus, investidor10, fundsexplorer)
llm/factory.ts       → create LangChain model (default: gemini-2.0-flash)
evaluation/analyzer.ts → LLM scores each asset against criteria (yes/no + justification, 0–10 score)
report/builder.ts    → assemble structured JSON report
scripts/upload-relatorio.ts → POST report to /avaliacao-fundamentalista
```

### Testing

- Integration tests extend `E2ETests` (base class using Testcontainers for MongoDB + Mockito for Redis)
- Tests are tagged `@Tag("e2e-test")`
- Test config: `infra/src/main/resources/application-test.yml`

### Deployment

- Fat JAR: `build/libs/application.jar`
- Procfile: `web: java -jar build/libs/application.jar`
- PORT env var controls listen port (defaults to 8080)
- MONGO_URL env var for MongoDB connection (defaults to `mongodb://localhost:27017/carteira`)
