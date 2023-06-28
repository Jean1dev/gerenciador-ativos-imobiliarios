package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.dominio.carteira.useCase.records.CriarOuAtualizarCarteiraInput;
import br.com.carteira.dominio.metas.Meta;

import java.util.Objects;

public class CriarEAtualizarCarteiraUserCase {

    private final CarteiraGateway carteiraGateway;

    public CriarEAtualizarCarteiraUserCase(CarteiraGateway carteiraGateway) {
        this.carteiraGateway = carteiraGateway;
    }

    public void executar(CriarOuAtualizarCarteiraInput input) {
        Carteira carteira = new Carteira();
        boolean atualizacao = false;
        if (Objects.nonNull(input.identificacaoCarteiraJaCriada())) {
            atualizacao = true;
            carteira = carteiraGateway.buscarCarteiraPeloId(input.identificacaoCarteiraJaCriada());
        }

        if (Objects.nonNull(input.nome()))
            carteira.setNome(input.nome());

        if (Objects.nonNull(input.meta())) {
            carteira.setMeta(input.meta());
        } else if (Objects.nonNull(input.metaDefinida())) {
            switch (input.metaDefinida()) {
                case META_DO_JEAN -> carteira.setMeta(Meta.metasDoJeanluca());
                case CONSERVADOR -> carteira.setMeta(Meta.conservador());
                case MODERADO -> carteira.setMeta(Meta.moderado());
                case ARROJADO -> carteira.setMeta(Meta.arrojado());
                case SOFISTICADO -> carteira.setMeta(Meta.sofisticado());
                case DINAMICO -> carteira.setMeta(Meta.dinamico());
            }

        }

        if (!atualizacao)
            carteira.setAtivos(Ativo.ativoSimplificadosToAtivo(input.ativos()));

        carteiraGateway.consolidar(carteiraGateway.salvar(carteira));
    }

}
