import { TavilySearch } from "@langchain/tavily";
import type { Config } from "../config/env.js";
import type { TipoAtivo } from "../types/api.js";
import { getUrlsParaAtivo, DOMINIOS_CONFIAVEIS } from "./sources.js";
import { logDebug, logInfo, logWarn } from "../logger.js";

const MAX_RESULTS = 8;

function normalizarResultadoTavily(
  result: unknown
): Array<{ url?: string; content?: string }> {
  if (Array.isArray(result)) {
    return result as Array<{ url?: string; content?: string }>;
  }
  if (result && typeof result === "object" && "results" in result) {
    const r = (result as { results: unknown }).results;
    return Array.isArray(r) ? (r as Array<{ url?: string; content?: string }>) : [];
  }
  if (result && typeof result === "object") {
    return [result as { url?: string; content?: string }];
  }
  return [];
}

export interface ResultadoPesquisa {
  conteudo: string;
  fontes: string[];
}

function buildSearchQueries(codigo: string, tipoAtivo: TipoAtivo): string[] {
  const codigoLower = codigo.toLowerCase();
  const queries: string[] = [];

  queries.push(`${codigo} fundamentus dados fundamentalistas último balanço`);
  queries.push(`${codigo} análise fundamentalista 2024 2025`);

  if (tipoAtivo === "FII") {
    queries.push(`${codigo} FII P/VP yield dividendos fundsexplorer`);
    queries.push(`${codigo} fundo imobiliário dividendos últimos anos`);
  }

  if (tipoAtivo === "ACAO_NACIONAL") {
    queries.push(`${codigo} ação resultados trimestrais indicadores`);
  }

  return queries;
}

export async function pesquisarAtivo(
  config: Config,
  codigo: string,
  tipoAtivo: TipoAtivo
): Promise<ResultadoPesquisa> {
  if (config.searchProvider !== "tavily") {
    logWarn("Search provider não é Tavily; retornando contexto mínimo", {
      searchProvider: config.searchProvider,
    });
    return {
      conteudo: `Ativo ${codigo} (${tipoAtivo}). Configure SEARCH_PROVIDER=tavily e TAVILY_API_KEY para pesquisa web.`,
      fontes: getUrlsParaAtivo(codigo, tipoAtivo),
    };
  }

  const tool = new TavilySearch({
    maxResults: MAX_RESULTS,
    includeDomains: DOMINIOS_CONFIAVEIS,
    searchDepth: "advanced",
  });

  const queries = buildSearchQueries(codigo, tipoAtivo);
  const fontes: string[] = [];
  const fragmentos: string[] = [];

  for (const query of queries) {
    logInfo("Executando pesquisa", { codigo, query });
    try {
      const result = await tool.invoke({ query });
      const results = normalizarResultadoTavily(result);
      for (const r of results) {
        if (r?.url) fontes.push(r.url);
        if (r?.content) fragmentos.push(r.content);
      }
    } catch (err) {
      logWarn("Erro em uma query de pesquisa", { query, codigo, err });
    }
  }

  const urlsConhecidas = getUrlsParaAtivo(codigo, tipoAtivo);
  const fontesUnicas = [...new Set([...urlsConhecidas, ...fontes])];
  const conteudo =
    fragmentos.length > 0
      ? fragmentos.join("\n\n---\n\n")
      : `Referências (sem conteúdo extraído): ${fontesUnicas.join(", ")}`;

  logDebug("Pesquisa concluída", {
    codigo,
    fragmentos: fragmentos.length,
    fontes: fontesUnicas.length,
  });

  return { conteudo, fontes: fontesUnicas };
}
