function getEnvOptional(key: string, defaultValue: string): string {
  return process.env[key] ?? defaultValue;
}

export function loadUploadConfig() {
  const apiBaseUrl = getEnvOptional("API_BASE_URL", "http://localhost:8080");
  const relatorioFile = getEnvOptional("RELATORIO_FILE", "relatorio.json");
  return { apiBaseUrl, relatorioFile };
}

export type UploadConfig = ReturnType<typeof loadUploadConfig>;
