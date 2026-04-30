package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.RendaFixa;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.metas.Meta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class NovoAporteUseCaseTest {

    /**
     * Cenário:
     * - VALE3  (ACAO_NACIONAL) : valor=50,   nota=8  → abaixo da meta de 20%
     * - BTC    (CRYPTO)        : valor=20,   nota=6  → abaixo da meta de 6%
     * - Tesouro(RENDA_FIXA)   : valor=1000, nota=5  → ACIMA da meta de 26% → não recebe aporte
     * <p>
     * Total carteira = 1070, aporte = 1000, projetado = 2070
     * <p>
     * Gaps positivos com ativos elegíveis:
     * ACAO_NACIONAL : 2070×0.20 − 50  =  364.0
     * CRYPTO        : 2070×0.06 − 20  =  104.2
     * Soma                             =  468.2
     * <p>
     * Aporte normalizado por classe:
     * ACAO_NACIONAL : (364.0/468.2) × 1000 ≈ 777.4  → VALE3 nota=8 → 100% → 777.4
     * CRYPTO        : (104.2/468.2) × 1000 ≈ 222.6  → BTC   nota=6 → 100% → 222.6
     * RENDA_FIXA    :  gap negativo → 0.0
     * <p>
     * Lista ordenada (ascendente): [0.0, 222.6, 777.4]
     * Soma total = 1000.0
     */
    @Test
    void deveDistribuirAportePorGapDeClasseEPonderarPorNota() {
        var carteira = new Carteira();
        carteira.setMeta(Meta.metasDoJeanluca());

        var vale3 = new AcaoNacional("VALE3", 0, 50, 8, 0, 1);
        var btc = new RendaFixa(TipoAtivo.CRYPTO, "BTC", 0, 20, 6, 0.0, 1);
        var tesouro = new RendaFixa(TipoAtivo.RENDA_FIXA, "Tesouro Nacional", 0, 1000, 5, 0.0, 1);

        carteira.setAtivos(Set.of(vale3, btc, tesouro));

        final var useCase = new NovoAporteUseCase();
        final var resultado = useCase.execute(1000.0, carteira);

        // Todos os valores devem ser não-negativos
        resultado.recomendacaoAporteList()
                .forEach(r -> Assertions.assertTrue(r.recomendacao().doubleValue() >= 0));
        resultado.metaComValorRecomendados()
                .forEach(m -> Assertions.assertTrue(m.valorRecomendado() >= 0));

        // A soma das recomendações por ativo deve ser igual ao valor do aporte
        var totalDistribuido = resultado.recomendacaoAporteList().stream()
                .mapToDouble(r -> r.recomendacao().doubleValue())
                .sum();
        Assertions.assertEquals(1000.0, totalDistribuido, 0.1);

        // Resultados ordenados de forma crescente pelo valor recomendado
        var recomendacoes = resultado.recomendacaoAporteList();

        // Tesouro: RENDA_FIXA acima da meta → não recebe aporte
        Assertions.assertEquals(0.0, recomendacoes.get(0).recomendacao().doubleValue());

        // BTC: CRYPTO abaixo da meta → recebe parte proporcional ao gap, nota=6 (único da classe)
        Assertions.assertEquals(222.6, recomendacoes.get(1).recomendacao().doubleValue());

        // VALE3: ACAO_NACIONAL abaixo da meta → recebe maior parte, nota=8 (único da classe)
        Assertions.assertEquals(777.4, recomendacoes.get(2).recomendacao().doubleValue());
    }

    /**
     * Cenário com múltiplos ativos na mesma classe para validar ponderação por nota.
     * <p>
     * - VALE3  (ACAO_NACIONAL, nota=7): valor=30  → gap positivo, peso=7/10=0.7
     * - PETR4  (ACAO_NACIONAL, nota=3): valor=20  → gap positivo, peso=3/10=0.3
     * - Tesouro(RENDA_FIXA,   nota=5): valor=500 → acima da meta → não recebe aporte
     * <p>
     * Total = 550, aporte = 500, projetado = 1050
     * <p>
     * Gap ACAO_NACIONAL : 1050×0.20 − 50 = 160   → elegível (somaNotas=10)
     * Gap RENDA_FIXA    : 1050×0.26 − 500 = -227  → negativo
     * <p>
     * Soma gaps elegíveis = 160 → ACAO_NACIONAL recebe 100% do aporte (500)
     * VALE3: 500 × (7/10) = 350.0
     * PETR4: 500 × (3/10) = 150.0
     * <p>
     * Lista ordenada: [0.0, 150.0, 350.0]
     */
    @Test
    void devePonderarPorNotaDentroDeUmaClasse() {
        var carteira = new Carteira();
        carteira.setMeta(Meta.metasDoJeanluca());

        var vale3 = new AcaoNacional("VALE3", 0, 30, 7, 0, 1);
        var petr4 = new AcaoNacional("PETR4", 0, 20, 3, 0, 1);
        var tesouro = new RendaFixa(TipoAtivo.RENDA_FIXA, "Tesouro Nacional", 0, 500, 5, 0.0, 1);

        carteira.setAtivos(Set.of(vale3, petr4, tesouro));

        final var resultado = new NovoAporteUseCase().execute(500.0, carteira);

        var totalDistribuido = resultado.recomendacaoAporteList().stream()
                .mapToDouble(r -> r.recomendacao().doubleValue())
                .sum();
        Assertions.assertEquals(500.0, totalDistribuido, 0.1);

        var recomendacoes = resultado.recomendacaoAporteList();

        // Tesouro: RENDA_FIXA acima da meta → 0
        Assertions.assertEquals(0.0, recomendacoes.get(0).recomendacao().doubleValue());

        // PETR4: menor nota (3/10) → recebe menos
        Assertions.assertEquals(150.0, recomendacoes.get(1).recomendacao().doubleValue());

        // VALE3: maior nota (7/10) → recebe mais
        Assertions.assertEquals(350.0, recomendacoes.get(2).recomendacao().doubleValue());
    }

    @Test
    void deveLancarExcecaoQuandoCarteiraTemValorTotalZero() {
        var carteira = new Carteira();
        carteira.setMeta(Meta.metasDoJeanluca());
        var ativo = new AcaoNacional("VALE3", 0, 0, 5, 0, 0);
        carteira.setAtivos(Set.of(ativo));

        Assertions.assertThrows(
                br.com.carteira.dominio.exception.DominioException.class,
                () -> new NovoAporteUseCase().execute(1000.0, carteira)
        );
    }

    @Test
    void deveLancarExcecaoQuandoCarteiraSeemMeta() {
        var carteira = new Carteira();
        var ativo = new AcaoNacional("VALE3", 0, 100, 5, 0, 1);
        carteira.setAtivos(Set.of(ativo));

        Assertions.assertThrows(
                br.com.carteira.dominio.exception.DominioException.class,
                () -> new NovoAporteUseCase().execute(1000.0, carteira)
        );
    }
}
