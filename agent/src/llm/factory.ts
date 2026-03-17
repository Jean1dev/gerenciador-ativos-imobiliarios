import type { BaseChatModel } from "@langchain/core/language_models/chat_models";
import { ChatGoogleGenerativeAI } from "@langchain/google-genai";
import type { Config } from "../config/env.js";
import { logInfo } from "../logger.js";

export function createLlm(config: Config): BaseChatModel {
  if (config.llmProvider === "gemini") {
    logInfo("Criando modelo LLM Gemini", {
      model: config.llmModel,
    });
    return new ChatGoogleGenerativeAI({
      model: config.llmModel,
      temperature: 0.2,
      maxRetries: 2,
      apiKey: config.googleApiKey || undefined,
    });
  }

  throw new Error(
    `LLM_PROVIDER não suportado: ${config.llmProvider}. Use "gemini" ou adicione outro provider em src/llm/factory.ts`
  );
}
