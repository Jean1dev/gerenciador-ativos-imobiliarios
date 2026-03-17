import type { AtivoDisponivel } from "../types/api.js";
import type { AvaliacaoAtivo, RelatorioFinal } from "../types/report.js";

export function buildAvaliacaoAtivo(
  ativo: AtivoDisponivel,
  nota: number,
  respostas: AvaliacaoAtivo["respostas"],
  fontes: string[]
): AvaliacaoAtivo {
  return {
    codigo: ativo.codigo,
    nome: ativo.nome,
    tipoAtivo: ativo.tipoAtivo,
    nota,
    respostas,
    dataAvaliacao: new Date().toISOString(),
    fontesUtilizadas: fontes,
  };
}

export function buildRelatorioFinal(avaliacoes: AvaliacaoAtivo[]): RelatorioFinal {
  return {
    geradoEm: new Date().toISOString(),
    ativos: avaliacoes,
  };
}
