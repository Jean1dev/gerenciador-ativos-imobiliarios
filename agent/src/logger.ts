const LEVELS = { debug: 0, info: 1, warn: 2, error: 3 } as const;
type Level = keyof typeof LEVELS;

let currentLevel: Level = "info";

export function setLogLevel(level: Level): void {
  currentLevel = level;
}

function shouldLog(level: Level): boolean {
  return LEVELS[level] >= LEVELS[currentLevel];
}

function formatMessage(level: string, message: string, meta?: unknown): string {
  const timestamp = new Date().toISOString();
  const metaStr = meta !== undefined ? ` ${JSON.stringify(meta)}` : "";
  return `[${timestamp}] [${level.toUpperCase()}] ${message}${metaStr}`;
}

export function logDebug(message: string, meta?: unknown): void {
  if (shouldLog("debug")) {
    console.debug(formatMessage("debug", message, meta));
  }
}

export function logInfo(message: string, meta?: unknown): void {
  if (shouldLog("info")) {
    console.info(formatMessage("info", message, meta));
  }
}

export function logWarn(message: string, meta?: unknown): void {
  if (shouldLog("warn")) {
    console.warn(formatMessage("warn", message, meta));
  }
}

export function logError(message: string, meta?: unknown): void {
  if (shouldLog("error")) {
    console.error(formatMessage("error", message, meta));
  }
}
