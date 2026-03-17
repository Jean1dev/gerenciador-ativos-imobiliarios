import type { BaseChatModel } from "@langchain/core/language_models/chat_models";
import { HumanMessage, SystemMessage } from "@langchain/core/messages";
import { z } from "zod";
import type { TipoAtivo } from "../types/api.js";
import type { RespostaCriterio } from "../types/report.js";
import { logDebug, logError, logInfo } from "../logger.js";

const RespostaCriterioSchema = z.object({
  pergunta: z.string(),
  resposta: z.boolean(),
  justificativa: z.string(),
});

const AvaliacaoLlmSchema = z.object({
  nota: z.number().min(0).max(10),
  respostas: z.array(RespostaCriterioSchema),
});

type AvaliacaoLlm = z.infer<typeof AvaliacaoLlmSchema>;

function buildSystemPrompt(tipoAtivo: TipoAtivo): string {
  const tipoLabel =
    tipoAtivo === "FII" ? "Fundo Imobiliário" : "Ação Nacional";
  return `Você é um analista fundamentalista de ativos brasileiros. Sua tarefa é avaliar um ${tipoLabel} com base em dados e notícias recentes (últimos balanços trimestrais, resultados dos últimos anos).

Regras:
- Responda SOMENTE com um JSON válido, sem texto antes ou depois.
- Use dados atuais: último balanço trimestral, resultados recentes, dividendos dos últimos anos.
- Para cada pergunta dos critérios: responda true (sim) ou false (não) e justifique em uma frase com base nos dados.
- Atribua uma nota de 0 a 10 para o ativo como um todo (fundamentalista), considerando quantos critérios foram atendidos e a qualidade dos indicadores.
- Se os dados não forem suficientes para uma pergunta, responda false e justifique "Dados insuficientes".
- O JSON deve ter exatamente: { "nota": number, "respostas": [ { "pergunta": string, "resposta": boolean, "justificativa": string } ] }`;
}

function buildUserPrompt(
  codigo: string,
  nome: string,
  tipoAtivo: TipoAtivo,
  perguntas: string[],
  contextoPesquisa: string
): string {
  return `Ativo: ${nome} (${codigo}) - Tipo: ${tipoAtivo}

Contexto obtido de fontes confiáveis (fundamentus, investidor10, fundsexplorer, etc.):

${contextoPesquisa}

---

Critérios a avaliar (responda cada um com true/false e justificativa breve):

${perguntas.map((p, i) => `${i + 1}. ${p}`).join("\n")}

---

Retorne APENAS o JSON no formato:
{"nota": <0-10>, "respostas": [{"pergunta": "<texto da pergunta>", "resposta": true|false, "justificativa": "<frase curta>"}]}`;
}

function parseJsonFromContent(content: string): AvaliacaoLlm {
  const trimmed = content.trim();
  const jsonStart = trimmed.indexOf("{");
  const jsonEnd = trimmed.lastIndexOf("}") + 1;
  if (jsonStart === -1 || jsonEnd <= jsonStart) {
    throw new Error("Resposta do LLM não contém JSON");
  }
  const jsonStr = trimmed.slice(jsonStart, jsonEnd);
  const parsed = JSON.parse(jsonStr) as unknown;
  return AvaliacaoLlmSchema.parse(parsed);
}

export async function analisarAtivo(
  llm: BaseChatModel,
  codigo: string,
  nome: string,
  tipoAtivo: TipoAtivo,
  perguntas: string[],
  contextoPesquisa: string,
  fontes: string[]
): Promise<{ nota: number; respostas: RespostaCriterio[] }> {
  logInfo("Iniciando análise fundamentalista", { codigo, tipoAtivo });

  const systemPrompt = buildSystemPrompt(tipoAtivo);
  const userPrompt = buildUserPrompt(
    codigo,
    nome,
    tipoAtivo,
    perguntas,
    contextoPesquisa.slice(0, 12000)
  );

  const messages = [
    new SystemMessage(systemPrompt),
    new HumanMessage(userPrompt),
  ];

  const response = await llm.invoke(messages);
  const content =
    typeof response.content === "string"
      ? response.content
      : Array.isArray(response.content)
        ? response.content
            .map((c: { type?: string; text?: string }) =>
              c && typeof c === "object" && "text" in c ? c.text : ""
            )
            .join("")
        : String(response.content);

  logDebug("Resposta bruta do LLM recebida", {
    codigo,
    length: content.length,
  });

  try {
    const avaliacao = parseJsonFromContent(content);
    const respostas: RespostaCriterio[] = avaliacao.respostas.map((r) => ({
      pergunta: r.pergunta,
      resposta: r.resposta,
      justificativa: r.justificativa,
    }));
    logInfo("Análise concluída", {
      codigo,
      nota: avaliacao.nota,
      respostasCount: respostas.length,
    });
    return {
      nota: avaliacao.nota,
      respostas,
    };
  } catch (err) {
    logError("Falha ao parsear resposta do LLM", { codigo, err, contentSlice: content.slice(0, 500) });
    throw err;
  }
}
