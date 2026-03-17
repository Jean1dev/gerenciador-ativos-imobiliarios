import type { TipoAtivo } from "../types/api.js";

const BASE_FUNDAMENTUS = "https://www.fundamentus.com.br/detalhes.php";
const BASE_INVESTIDOR10_ACOES = "https://investidor10.com.br/acoes";
const BASE_FUNDS_EXPLORER = "https://www.fundsexplorer.com.br/funds";

export function getUrlsParaAtivo(codigo: string, tipoAtivo: TipoAtivo): string[] {
  const codigoLower = codigo.toLowerCase();
  const urls: string[] = [];

  urls.push(`${BASE_FUNDAMENTUS}?papel=${codigoLower}`);

  if (tipoAtivo === "ACAO_NACIONAL") {
    urls.push(`${BASE_INVESTIDOR10_ACOES}/${codigoLower}/`);
  }

  if (tipoAtivo === "FII") {
    urls.push(`${BASE_FUNDS_EXPLORER}/${codigoLower}`);
  }

  return urls;
}

export const DOMINIOS_CONFIAVEIS = [
  "fundamentus.com.br",
  "investidor10.com.br",
  "fundsexplorer.com.br",
  "b3.com.br",
  "infomoney.com.br",
  "valor.globo.com",
  "economia.estadao.com.br",
];
