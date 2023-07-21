package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.ativo.RendaFixa;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.metas.Meta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

@DisplayName("Carlcular Percentual da Carteira")
class CalcularPercentualCarteiraEmMetasUseCaseTest {

    @Test
    @DisplayName("Deve calcular corretamente")
    public void deveCalcular() {
        final var useCase = new CalcularPercentualCarteiraEmMetasUseCase();
        HashSet<Ativo> ativos = new HashSet<>();
        var carteira = new Carteira();
        carteira.setMeta(Meta.metasDoJeanluca());
        var vale3 = new AcaoNacional(
                "VALE3",
                0,
                25.4,
                5,
                0,
                2
        );

        ativos.add(vale3);
        carteira.setAtivos(ativos);
        var resultado = useCase.executar(carteira);
        Assertions.assertEquals(100.0, resultado.get(TipoAtivo.ACAO_NACIONAL.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.FII.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.CRYPTO.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.RENDA_FIXA.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.ACAO_INTERNACIONAL.descricao()));

        var tesouroNacional = new RendaFixa(
                TipoAtivo.RENDA_FIXA,
                "Tesouro Nacional",
                0.0,
                1500,
                4,
                0.0,
                1
        );

        ativos.add(tesouroNacional);
        carteira.setAtivos(ativos);
        resultado = useCase.executar(carteira);
        Assertions.assertEquals(96.7, resultado.get(TipoAtivo.RENDA_FIXA.descricao()));
        Assertions.assertEquals(3.3, resultado.get(TipoAtivo.ACAO_NACIONAL.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.FII.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.CRYPTO.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.ACAO_INTERNACIONAL.descricao()));

        var crypto = new RendaFixa(
                TipoAtivo.CRYPTO,
                "Renda fix em crypto",
                0.0,
                150,
                4,
                0.0,
                2
        );

        ativos.add(crypto);
        carteira.setAtivos(ativos);
        resultado = useCase.executar(carteira);
        Assertions.assertEquals(81.0, resultado.get(TipoAtivo.RENDA_FIXA.descricao()));
        Assertions.assertEquals(2.7, resultado.get(TipoAtivo.ACAO_NACIONAL.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.FII.descricao()));
        Assertions.assertEquals(16.2, resultado.get(TipoAtivo.CRYPTO.descricao()));
        Assertions.assertEquals(0.0, resultado.get(TipoAtivo.ACAO_INTERNACIONAL.descricao()));
    }

}