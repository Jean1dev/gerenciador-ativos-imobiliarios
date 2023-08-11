package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.RendaFixa;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.metas.Meta;
import org.junit.jupiter.api.Test;

import java.util.Set;

class NovoAporteUseCaseTest {

    @Test
    void execute() {
        var carteira = new Carteira();
        carteira.setMeta(Meta.metasDoJeanluca());
        var vale3 = new AcaoNacional(
                "VALE3",
                33.3,
                25.4,
                5,
                0,
                2
        );
        var tesouroNacional = new RendaFixa(
                TipoAtivo.RENDA_FIXA,
                "Tesouro Nacional",
                33.3,
                1500,
                4,
                0.0,
                1
        );
        var crypto = new RendaFixa(
                TipoAtivo.CRYPTO,
                "Renda fix em crypto",
                33.3,
                150,
                4,
                0.0,
                2
        );
        carteira.setAtivos(Set.of(vale3, tesouroNacional, crypto));
        final var useCase = new NovoAporteUseCase();

        final var recomendacaoAportes = useCase.execute(1500.00, carteira);
        System.out.println(recomendacaoAportes);
    }
}