import "dotenv/config";
import { readFile } from "fs/promises";
import { loadUploadConfig } from "./upload-config.js";
import { logError, logInfo } from "../logger.js";
import type { RelatorioFinal } from "../types/report.js";

async function main(): Promise<void> {
  const config = loadUploadConfig();
  const url = `${config.apiBaseUrl}/avaliacao-fundamentalista`;
  logInfo("Enviando relatório para a API", {
    url,
    arquivo: config.relatorioFile,
  });

  let conteudo: string;
  try {
    conteudo = await readFile(config.relatorioFile, "utf-8");
  } catch (err) {
    logError("Falha ao ler arquivo do relatório", {
      path: config.relatorioFile,
      err,
    });
    process.exit(1);
  }

  let relatorio: RelatorioFinal;
  try {
    relatorio = JSON.parse(conteudo) as RelatorioFinal;
  } catch (err) {
    logError("Falha ao parsear JSON do relatório", { err });
    process.exit(1);
  }

  if (!relatorio.ativos?.length) {
    logInfo("Relatório sem ativos; nenhum dado enviado");
    return;
  }

  const response = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(relatorio),
  });

  if (!response.ok) {
    logError("Falha ao enviar relatório", {
      status: response.status,
      statusText: response.statusText,
      body: await response.text(),
    });
    process.exit(1);
  }

  logInfo("Relatório enviado com sucesso", {
    ativos: relatorio.ativos.length,
    geradoEm: relatorio.geradoEm,
  });
}

main();
