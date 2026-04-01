export type TipoAtivo = "ACAO_NACIONAL" | "FII";

export interface AtivoDisponivel {
  imgUrl: string | null;
  nome: string;
  codigo: string;
  disponivel: boolean;
  valor: number;
  variacao: number;
  variacaoPositiva: boolean;
  tipoAtivo: TipoAtivo;
}

export interface CriterioResponse {
  criterio: string;
  pergunta: string;
  simOuNao: boolean;
}
