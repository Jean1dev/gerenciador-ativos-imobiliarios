# Especificação de Negócio — Novo Aporte com Balanceamento de Carteira

## Objetivo

Calcular como distribuir um novo aporte financeiro entre os ativos da carteira de forma a:

1. Reduzir o desvio entre a alocação atual e a meta de alocação por classe de ativos
2. Priorizar ativos com melhor avaliação (nota) dentro de cada classe

## Entradas

| Campo         | Tipo       | Descrição                                      |
|---------------|------------|------------------------------------------------|
| `valorAporte` | `Double`   | Valor total a ser investido                    |
| `carteira`    | `Carteira` | Carteira do usuário com ativos e meta definida |

A carteira deve ter:

- Uma **meta** (`Meta`) com o percentual-alvo por `TipoAtivo`
- Pelo menos um ativo com `valorAtual > 0` e `quantidade > 0` (valor total > 0)

## Algoritmo

### Passo 1 — Valor total e projetado

```
totalCarteira       = Σ (ativo.valorAtual × ativo.quantidade)
valorTotalProjetado = totalCarteira + valorAporte
```

### Passo 2 — Gap por classe de ativos

Para cada classe (`TipoAtivo`) definida na meta:

```
valorIdealClasse = valorTotalProjetado × (percentualAlvo / 100)
valorAtualClasse = Σ (ativo.valorAtual × ativo.quantidade) para ativos dessa classe
gapClasse        = valorIdealClasse − valorAtualClasse
```

- `gapClasse > 0` → classe está abaixo da meta (precisa de aporte)
- `gapClasse ≤ 0` → classe está na meta ou acima (não recebe aporte)

### Passo 3 — Elegibilidade das classes

Uma classe é **elegível** para receber aporte se:

- `gapClasse > 0`, **E**
- existe ao menos um ativo da classe na carteira com `nota > 0`

Classes com gap positivo mas sem ativos elegíveis são descartadas — o capital correspondente é redistribuído entre as
demais classes elegíveis pela normalização do passo 4.

### Passo 4 — Normalização do aporte por classe

```
somaGapsElegiveis = Σ gapClasse  (apenas classes elegíveis)

aporteClasse = (gapClasse / somaGapsElegiveis) × valorAporte
```

Isso garante que a soma de todos os `aporteClasse` seja exatamente `valorAporte`.

### Passo 5 — Distribuição dentro da classe por nota

Para cada ativo de uma classe elegível:

```
somaNotasClasse = Σ ativo.nota  (apenas ativos com nota > 0 na classe)
pesoAtivo       = ativo.nota / somaNotasClasse
aporteAtivo     = aporteClasse × pesoAtivo
```

Ativos com `nota = 0` são ignorados e não recebem aporte.

## Saída

Lista de `RecomendacaoAporte` ordenada de forma crescente pelo valor recomendado:

```
[
  { ativo: <Ativo>, recomendacao: <BigDecimal> },
  ...
]
```

Adicionalmente, o resultado inclui `MetaComValorRecomendado` por classe, contendo o valor normalizado do aporte
destinado a cada `TipoAtivo`.

## Restrições

- Não há venda de ativos
- A soma total das recomendações por ativo é igual a `valorAporte` (variações de até 0,1 por arredondamento)
- Ativos com `nota = 0` não recebem capital
- Se nenhuma classe for elegível (todas com gap negativo ou sem ativos), nenhum ativo recebe aporte

## Exemplo

### Dados

| Ativo         | Classe        | valorAtual | quantidade | nota | Valor total |
|---------------|---------------|------------|------------|------|-------------|
| VALE3         | ACAO_NACIONAL | 50         | 1          | 8    | 50          |
| BTC           | CRYPTO        | 20         | 1          | 6    | 20          |
| Tesouro Selic | RENDA_FIXA    | 1000       | 1          | 5    | 1000        |

- **Total carteira:** 1.070
- **Aporte:** 1.000
- **Total projetado:** 2.070
- **Meta:** `metasDoJeanluca` (ACAO_NACIONAL=20%, CRYPTO=6%, RENDA_FIXA=26%, …)

### Cálculo dos gaps

| Classe             | Ideal (2070 × %) | Atual    | Gap                               |
|--------------------|------------------|----------|-----------------------------------|
| ACAO_NACIONAL      | 414,00           | 50,00    | **+364,00** ✓                     |
| ACAO_INTERNACIONAL | 621,00           | 0,00     | +621,00 (sem ativos → descartado) |
| FII                | 372,60           | 0,00     | +372,60 (sem ativos → descartado) |
| CRYPTO             | 124,20           | 20,00    | **+104,20** ✓                     |
| RENDA_FIXA         | 538,20           | 1.000,00 | −461,80 → 0                       |

### Normalização

```
somaGapsElegiveis = 364,00 + 104,20 = 468,20

aporteClasse ACAO_NACIONAL = (364,00 / 468,20) × 1000 ≈ 777,4
aporteClasse CRYPTO        = (104,20 / 468,20) × 1000 ≈ 222,6
```

### Resultado final

| Ativo         | Recomendação                                      |
|---------------|---------------------------------------------------|
| VALE3         | R$ 777,40 (único ativo elegível em ACAO_NACIONAL) |
| BTC           | R$ 222,60 (único ativo elegível em CRYPTO)        |
| Tesouro Selic | R$ 0,00 (classe acima da meta)                    |

**Soma: R$ 1.000,00** ✓

## Implementação

| Artefato                           | Localização                                                                  |
|------------------------------------|------------------------------------------------------------------------------|
| Orquestração principal             | `dominio/.../carteira/useCase/NovoAporteUseCase.java`                        |
| Distribuição por nota              | `dominio/.../ativo/useCase/CalcularRecomendacaoAporteUseCase.java`           |
| Cálculo de percentuais da carteira | `dominio/.../carteira/useCase/CalcularPercentualCarteiraEmMetasUseCase.java` |
| Testes                             | `dominio/src/test/.../carteira/useCase/NovoAporteUseCaseTest.java`           |
