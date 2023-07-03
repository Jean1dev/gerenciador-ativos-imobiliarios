package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.ativo.AtivosComTickerGateway;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.exception.DominioException;

import java.util.Objects;
import java.util.Set;

public class GestaoAtivosUseCase {

    private final CarteiraGateway carteiraGateway;
    private final AtivosComTickerGateway ativosComTickerGateway;

    public GestaoAtivosUseCase(CarteiraGateway carteiraGateway, AtivosComTickerGateway ativosComTickerGateway) {
        this.carteiraGateway = carteiraGateway;
        this.ativosComTickerGateway = ativosComTickerGateway;
    }

    public void adicionar(AdicionarAtivoInput input) {
        Objects.requireNonNull(input.tipoAtivo(), "Tipo ativo não pode ser null");
        Carteira carteira = carteiraGateway.buscarCarteiraPeloId(input.identificacaoCarteira());
        if (isAtivoComticker(input)) {
            adicionarAtivoComTicker(carteira, input.tipoAtivo(), input.nome(), input.quantidade(), input.nota());
            return;
        }

        carteiraGateway.adicionarAtivoNaCarteira(carteira,
                new AtivoSimplificado(
                        input.tipoAtivo(),
                        input.nome(),
                        input.quantidade(),
                        input.nota()
                ));
    }

    private void adicionarAtivoComTicker(
            Carteira carteira,
            TipoAtivo tipoAtivo,
            String nome,
            Double quantidade,
            Integer nota) {
        Objects.requireNonNull(carteira, "carteira nao pode ser null");
        Objects.requireNonNull(tipoAtivo, "tipoAtivo nao pode ser null");
        Objects.requireNonNull(nome, "Ticker nao pode ser null");
        Objects.requireNonNull(quantidade, "quantidade nao pode ser null");
        Objects.requireNonNull(nota, "nota nao pode ser null");
        ativosComTickerGateway.buscarPeloTicker(nome)
                .ifPresentOrElse(
                        ativo -> {
                            if (carteiraGateway.verificarSeJaExisteTickerNaCarteira(carteira, nome)) {
                                throw new DominioException("Ativo já adicionado nessa carteira");
                            }

                            carteiraGateway.adicionarAtivoNaCarteira(carteira,
                                    new AtivoSimplificado(
                                            tipoAtivo,
                                            nome.toUpperCase(),
                                            quantidade,
                                            nota
                                    ));

                            var carteiraParaConsolidar = new Carteira();
                            carteiraParaConsolidar.setNome(carteira.getNome());
                            carteiraParaConsolidar.setIdentificacao(carteira.getIdentificacao());
                            carteiraParaConsolidar.setAtivos(Set.of(ativo));
                            carteiraGateway.consolidar(carteiraParaConsolidar);
                        },
                        () -> {
                            ativosComTickerGateway.adicionarParaMonitoramento(nome, tipoAtivo);
                            carteiraGateway.adicionarAtivoNaCarteira(carteira,
                                    new AtivoSimplificado(
                                            tipoAtivo,
                                            nome,
                                            quantidade,
                                            nota
                                    ));
                        }
                );
    }

    private boolean isAtivoComticker(AdicionarAtivoInput input) {
        return TipoAtivo.ACAO_NACIONAL.equals(input.tipoAtivo())
                || TipoAtivo.ACAO_INTERNACIONAL.equals(input.tipoAtivo())
                || TipoAtivo.FII.equals(input.tipoAtivo());
    }
}
