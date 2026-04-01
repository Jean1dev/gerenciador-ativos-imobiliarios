# Agente de Análise de Ativos Brasileiros

Projeto em TypeScript + LangChain que consulta a API do gerenciador de ativos, pesquisa dados fundamentalistas em fontes confiáveis e gera um relatório em JSON com nota por ativo.

## Pré-requisitos

- Node.js >= 20
- API Java rodando em `localhost:8080` (ou `API_BASE_URL`)
- Chave da API Google (Gemini) em `GOOGLE_API_KEY`
- Chave Tavily (pesquisa web) em `TAVILY_API_KEY` quando `SEARCH_PROVIDER=tavily`

## Instalação

```bash
cd agent
npm install
```

## Variáveis de ambiente

Copie `.env.example` para `.env` e preencha:

| Variável | Obrigatória | Descrição |
|----------|-------------|-----------|
| `API_BASE_URL` | Não (default: http://localhost:8080) | Base URL da API Java |
| `GOOGLE_API_KEY` | Sim (se Gemini) | Chave da API Google para Gemini |
| `TAVILY_API_KEY` | Sim (se Tavily) | Chave Tavily para pesquisa web |
| `LLM_PROVIDER` | Não (default: gemini) | Provider do modelo (ex.: gemini) |
| `LLM_MODEL` | Não (default: gemini-2.0-flash) | Nome do modelo |
| `SEARCH_PROVIDER` | Não (default: tavily) | Provedor de busca (tavily) |
| `LOG_LEVEL` | Não (default: info) | debug, info, warn, error |
| `OUTPUT_FILE` | Não (default: relatorio.json) | Caminho do arquivo onde o relatório JSON será gravado |
| `RELATORIO_FILE` | Não (default: relatorio.json) | Caminho do arquivo lido pelo script de upload para a API |

## Execução

```bash
npm run build
npm start
```

Ou em desenvolvimento:

```bash
npm run dev
```

O relatório é gravado em JSON no arquivo indicado por `OUTPUT_FILE` (por padrão `relatorio.json`).

### Enviar relatório para a API (front-ends)

Após gerar o relatório, envie-o para a API Java para disponibilizar nos front-ends:

```bash
npm run upload-relatorio
```

O script lê o arquivo em `RELATORIO_FILE` (default: `relatorio.json`) e faz POST em `{API_BASE_URL}/avaliacao-fundamentalista`. A API expõe também GET `{API_BASE_URL}/avaliacao-fundamentalista` para listar as avaliações.

## Fluxo

1. **Listagem**: GET `{API_BASE_URL}/marketplace/listagem-ativos` para obter ativos a analisar.
2. **Critérios**: Para cada ativo, GET `{API_BASE_URL}/criterio?tipo=FII` ou `tipo=ACAO_NACIONAL` para obter as perguntas de critério (campo `pergunta`).
3. **Pesquisa**: Busca web (Tavily) em domínios como fundamentus.com.br, investidor10.com.br, fundsexplorer.com.br, com foco em dados atuais (último balanço trimestral, resultados recentes).
4. **Análise**: O LLM (Gemini por padrão) responde cada pergunta com sim/não e justificativa e atribui uma nota de 0 a 10.
5. **Relatório**: JSON com `geradoEm`, `ativos[]` (codigo, nome, tipoAtivo, nota, respostas, dataAvaliacao, fontesUtilizadas).

## Trocar de modelo

Altere `LLM_PROVIDER` e `LLM_MODEL`. Para usar outro provider (ex.: OpenAI), adicione o caso em `src/llm/factory.ts` e as variáveis de ambiente correspondentes.

## Estrutura do projeto

- `src/config/env.ts` – Carregamento e validação de variáveis de ambiente
- `src/api/client.ts` – Cliente da API (listagem e critérios)
- `src/research/sources.ts` – URLs e domínios confiáveis por tipo de ativo
- `src/research/search.ts` – Pesquisa web (Tavily)
- `src/llm/factory.ts` – Criação do modelo LLM (Gemini; extensível)
- `src/evaluation/analyzer.ts` – Análise fundamentalista via LLM e saída estruturada
- `src/report/builder.ts` – Montagem do relatório final
- `src/pipeline/orchestrator.ts` – Orquestração do fluxo
- `src/index.ts` – Entrada e gravação do relatório em arquivo
- `src/scripts/upload-config.ts` – Config do script de upload (API_BASE_URL, RELATORIO_FILE)
- `src/scripts/upload-relatorio.ts` – Lê o JSON e envia POST para a API
