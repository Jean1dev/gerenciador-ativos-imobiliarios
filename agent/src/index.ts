import "dotenv/config";
import { writeFile } from "fs/promises";
import { loadConfig } from "./config/env.js";
import { executarPipeline } from "./pipeline/orchestrator.js";
import { logError, logInfo } from "./logger.js";

async function main(): Promise<void> {
  let config;
  try {
    config = loadConfig();
  } catch (err) {
    logError("Configuração inválida", { err });
    process.exit(1);
  }

  try {
    const relatorio = await executarPipeline(config);
    const output = JSON.stringify(relatorio, null, 2);
    await writeFile(config.outputFile, output, "utf-8");
    logInfo("Relatório gravado em arquivo", { path: config.outputFile });
  } catch (err) {
    logError("Falha na execução do pipeline", { err });
    process.exit(1);
  }
}

main();
