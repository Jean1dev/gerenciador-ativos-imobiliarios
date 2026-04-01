import type { Config } from "../config/env.js";
import type { AtivoDisponivel } from "../types/api.js";
import type { AvaliacaoAtivo, RelatorioFinal } from "../types/report.js";
import { fetchListagemAtivos, fetchCriteriosPorTipo } from "../api/client.js";
import { pesquisarAtivo } from "../research/search.js";
import { createLlm } from "../llm/factory.js";
import { analisarAtivo } from "../evaluation/analyzer.js";
import { buildAvaliacaoAtivo, buildRelatorioFinal } from "../report/builder.js";
import { logError, logInfo, logWarn } from "../logger.js";
import { setLogLevel } from "../logger.js";

export async function executarPipeline(config: Config): Promise<RelatorioFinal> {
  const level = config.logLevel as "debug" | "info" | "warn" | "error";
  setLogLevel(level);

  logInfo("Iniciando pipeline de análise de ativos", {
    apiBaseUrl: config.apiBaseUrl,
    llmProvider: config.llmProvider,
    searchProvider: config.searchProvider,
  });

  const ativos = await fetchListagemAtivos(config);
  if (ativos.length === 0) {
    logWarn("Nenhum ativo retornado pela API; relatório vazio");
    return buildRelatorioFinal([]);
  }

  const llm = createLlm(config);
  const avaliacoes: AvaliacaoAtivo[] = [];

  for (const ativo of ativos) {
    try {
      const avaliacao = await processarAtivo(config, llm, ativo);
      avaliacoes.push(avaliacao);
    } catch (err) {
      logError("Erro ao processar ativo; pulando", {
        codigo: ativo.codigo,
        err,
      });
    }
  }

  const relatorio = buildRelatorioFinal(avaliacoes);
  logInfo("Pipeline finalizado", {
    totalAtivos: ativos.length,
    avaliados: avaliacoes.length,
  });
  return relatorio;
}

async function processarAtivo(
  config: Config,
  llm: ReturnType<typeof createLlm>,
  ativo: AtivoDisponivel
): Promise<AvaliacaoAtivo> {
  logInfo("Processando ativo", {
    codigo: ativo.codigo,
    nome: ativo.nome,
    tipoAtivo: ativo.tipoAtivo,
  });

  const perguntas = await fetchCriteriosPorTipo(config, ativo.tipoAtivo);
  if (perguntas.length === 0) {
    logWarn("Nenhum critério para o tipo; usando nota 0", {
      codigo: ativo.codigo,
      tipoAtivo: ativo.tipoAtivo,
    });
    return buildAvaliacaoAtivo(ativo, 0, [], []);
  }

  const { conteudo, fontes } = await pesquisarAtivo(
    config,
    ativo.codigo,
    ativo.tipoAtivo
  );

  const { nota, respostas } = await analisarAtivo(
    llm,
    ativo.codigo,
    ativo.nome,
    ativo.tipoAtivo,
    perguntas,
    conteudo,
    fontes
  );

  return buildAvaliacaoAtivo(ativo, nota, respostas, fontes);
}
