import type { TipoAtivo } from "./api.js";

export interface RespostaCriterio {
  pergunta: string;
  resposta: boolean;
  justificativa: string;
}

export interface AvaliacaoAtivo {
  codigo: string;
  nome: string;
  tipoAtivo: TipoAtivo;
  nota: number;
  respostas: RespostaCriterio[];
  dataAvaliacao: string;
  fontesUtilizadas: string[];
}

export interface RelatorioFinal {
  geradoEm: string;
  ativos: AvaliacaoAtivo[];
}
