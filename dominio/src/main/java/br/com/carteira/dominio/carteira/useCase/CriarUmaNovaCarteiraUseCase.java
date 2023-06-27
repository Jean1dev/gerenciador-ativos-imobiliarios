package br.com.carteira.dominio.carteira.useCase;

import br.com.carteira.dominio.ativo.AcaoInternacional;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.carteira.useCase.records.CriarCarteiraInput;
import br.com.carteira.dominio.metas.Meta;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CriarUmaNovaCarteiraUseCase {

    private final CarteiraGateway carteiraGateway;

    public CriarUmaNovaCarteiraUseCase(CarteiraGateway carteiraGateway) {
        this.carteiraGateway = carteiraGateway;
    }

    public void executar(CriarCarteiraInput input) {
        Carteira carteira = new Carteira();
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

        carteira.setAtivos(toAtivos(input.ativos()));

        carteiraGateway.consolidar(carteiraGateway.salvar(carteira));
    }

    private Set<Ativo> toAtivos(Collection<AtivoSimplificado> ativos) {
        if (Objects.isNull(ativos)) {
            return Collections.emptySet();
        }

        return ativos.stream().map(ativoSimplificado -> {
            switch (ativoSimplificado.tipoAtivo()) {
                case ACAO_NACIONAL -> {
                    return AcaoNacional.fromSimplificado(ativoSimplificado);
                }
                case ACAO_INTERNACIONAL -> {
                    return AcaoInternacional.fromSimplificado(ativoSimplificado);
                }
                default -> {
                    return null;
                }
            }
        }).collect(Collectors.toUnmodifiableSet());
    }
}
