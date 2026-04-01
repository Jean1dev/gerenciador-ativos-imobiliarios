function getEnv(key: string): string {
  const value = process.env[key];
  if (value === undefined || value === "") {
    throw new Error(`Variável de ambiente obrigatória não definida: ${key}`);
  }
  return value;
}

function getEnvOptional(key: string, defaultValue: string): string {
  return process.env[key] ?? defaultValue;
}

export function loadConfig() {
  const apiBaseUrl = getEnvOptional("API_BASE_URL", "http://localhost:8080");
  const googleApiKey = getEnvOptional("GOOGLE_API_KEY", "");
  const tavilyApiKey = getEnvOptional("TAVILY_API_KEY", "");
  const llmProvider = getEnvOptional("LLM_PROVIDER", "gemini");
  const llmModel = getEnvOptional("LLM_MODEL", "gemini-2.0-flash");
  const searchProvider = getEnvOptional("SEARCH_PROVIDER", "tavily");
  const logLevel = getEnvOptional("LOG_LEVEL", "info");
  const outputFile = getEnvOptional("OUTPUT_FILE", "relatorio.json");

  if (llmProvider === "gemini" && !googleApiKey) {
    throw new Error("GOOGLE_API_KEY é obrigatória quando LLM_PROVIDER=gemini");
  }
  if (searchProvider === "tavily" && !tavilyApiKey) {
    throw new Error("TAVILY_API_KEY é obrigatória quando SEARCH_PROVIDER=tavily");
  }

  return {
    apiBaseUrl,
    googleApiKey,
    tavilyApiKey,
    llmProvider,
    llmModel,
    searchProvider,
    logLevel,
    outputFile,
  };
}

export type Config = ReturnType<typeof loadConfig>;
