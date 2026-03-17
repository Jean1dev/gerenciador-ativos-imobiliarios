import type { Config } from "../config/env.js";
import type { AtivoDisponivel, CriterioResponse, TipoAtivo } from "../types/api.js";
import { logDebug, logError, logInfo } from "../logger.js";

export async function fetchListagemAtivos(config: Config): Promise<AtivoDisponivel[]> {
  const url = `${config.apiBaseUrl}/marketplace/listagem-ativos`;
  logInfo("Buscando listagem de ativos", { url });

  const response = await fetch(url);
  if (!response.ok) {
    logError("Falha ao buscar listagem de ativos", {
      status: response.status,
      statusText: response.statusText,
    });
    throw new Error(`Listagem ativos: ${response.status} ${response.statusText}`);
  }

  const data = (await response.json()) as AtivoDisponivel[];
  logInfo("Listagem de ativos obtida", { quantidade: data.length });
  logDebug("Ativos", { codigos: data.map((a) => a.codigo) });
  return data;
}

export async function fetchCriteriosPorTipo(
  config: Config,
  tipo: TipoAtivo
): Promise<string[]> {
  const url = `${config.apiBaseUrl}/criterio?tipo=${encodeURIComponent(tipo)}`;
  logInfo("Buscando critérios por tipo", { tipo, url });

  const response = await fetch(url);
  if (!response.ok) {
    logError("Falha ao buscar critérios", {
      tipo,
      status: response.status,
      statusText: response.statusText,
    });
    throw new Error(`Criterios ${tipo}: ${response.status} ${response.statusText}`);
  }

  const data = (await response.json()) as CriterioResponse[];
  const perguntas = data.map((c) => c.pergunta).filter(Boolean);
  logInfo("Critérios obtidos", { tipo, quantidade: perguntas.length });
  return perguntas;
}
