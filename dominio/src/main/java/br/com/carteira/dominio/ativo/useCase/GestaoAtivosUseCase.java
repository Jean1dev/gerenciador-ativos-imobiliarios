package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.ativo.AtivosComTickerGateway;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AtualizarAtivoInput;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.carteira.CarteiraGateway;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.crypto.CryptoAtivosMapping;
import br.com.carteira.dominio.exception.DominioException;

import java.util.Objects;
import java.util.Set;

import static br.com.carteira.dominio.Utils.nullOrValue;

public class GestaoAtivosUseCase {

    private final CarteiraGateway carteiraGateway;
    private final AtivosComTickerGateway ativosComTickerGateway;

    public GestaoAtivosUseCase(CarteiraGateway carteiraGateway, AtivosComTickerGateway ativosComTickerGateway) {
        this.carteiraGateway = carteiraGateway;
        this.ativosComTickerGateway = ativosComTickerGateway;
    }

    public void atualizarAtivo(AtualizarAtivoInput input) {
        ativosComTickerGateway.atualizarAtivo(input);
        carteiraGateway.consolidar(carteiraGateway.buscarCarteiraPeloAtivo(input.identificacao()));
    }

    public void removerAtivo(String identificacao) {
        var carteira = carteiraGateway.buscarCarteiraPeloAtivo(identificacao);
        carteira.setQuantidadeAtivos(carteira.getQuantidadeAtivos() - 1);
        carteiraGateway.salvar(carteira);
        carteiraGateway.deletarAtivo(identificacao);
    }

    public void adicionar(AdicionarAtivoInput input) {
        Objects.requireNonNull(input.tipoAtivo(), "Tipo ativo não pode ser null");
        var nota = (Integer) nullOrValue(input.nota(), 0);
        Carteira carteira = carteiraGateway.buscarCarteiraPeloId(input.identificacaoCarteira());
        if (isAtivoComticker(input)) {
            adicionarAtivoComTicker(carteira, new AtivoSimplificado(
                    input.tipoAtivo(),
                    input.nome().toUpperCase().trim(),
                    input.quantidade(),
                    nota,
                    input.criterios()
            ));
            carteira.setQuantidadeAtivos(carteira.getQuantidadeAtivos() + 1);
            carteiraGateway.salvar(carteira);
            return;
        } else if (TipoAtivo.CRYPTO.equals(input.tipoAtivo())) {
            input = resolverNomeCrypto(input);
        }

        carteira.setQuantidadeAtivos(carteira.getQuantidadeAtivos() + 1);
        carteiraGateway.salvar(carteira);
        carteiraGateway.adicionarAtivoNaCarteira(carteira,
                new AtivoSimplificado(
                        input.tipoAtivo(),
                        input.nome(),
                        input.quantidade(),
                        nota,
                        null,
                        (double) nullOrValue(input.valorAtual(), 0.0)
                ));
    }

    private AdicionarAtivoInput resolverNomeCrypto(AdicionarAtivoInput input) {
        return CryptoAtivosMapping.ifContainsGetName(input.nome())
                .map(nome -> new AdicionarAtivoInput(
                        input.tipoAtivo(),
                        input.nota(),
                        input.valorAtual(),
                        input.localAlocado(),
                        input.tipoAlocacao(),
                        input.quantidade(),
                        nome,
                        input.identificacaoCarteira(),
                        input.criterios()
                ))
                .orElseThrow(() -> new DominioException("criptomoeda ainda nao suportada"));
    }

    private void adicionarAtivoComTicker(Carteira carteira, AtivoSimplificado ativoSimplificado) {
        Objects.requireNonNull(carteira, "carteira nao pode ser null");
        Objects.requireNonNull(ativoSimplificado.tipoAtivo(), "tipoAtivo nao pode ser null");
        Objects.requireNonNull(ativoSimplificado.papel(), "Ticker nao pode ser null");
        Objects.requireNonNull(ativoSimplificado.quantidade(), "quantidade nao pode ser null");
        Objects.requireNonNull(ativoSimplificado.nota(), "nota nao pode ser null");
        ativosComTickerGateway.buscarPeloTicker(ativoSimplificado.papel())
                .ifPresentOrElse(
                        ativo -> {
                            if (carteiraGateway.verificarSeJaExisteTickerNaCarteira(carteira, ativoSimplificado.papel())) {
                                throw new DominioException("Ativo já adicionado nessa carteira");
                            }

                            carteiraGateway.adicionarAtivoNaCarteira(carteira, ativoSimplificado);

                            var carteiraParaConsolidar = new Carteira();
                            carteiraParaConsolidar.setNome(carteira.getNome());
                            carteiraParaConsolidar.setIdentificacao(carteira.getIdentificacao());
                            carteiraParaConsolidar.setAtivos(Set.of(ativo));
                            carteiraGateway.consolidar(carteiraParaConsolidar);
                        },
                        () -> {
                            ativosComTickerGateway.adicionarParaMonitoramento(ativoSimplificado.papel(), ativoSimplificado.tipoAtivo());
                            carteiraGateway.adicionarAtivoNaCarteira(carteira, ativoSimplificado);
                        }
                );
    }

    private boolean isAtivoComticker(AdicionarAtivoInput input) {
        return TipoAtivo.ACAO_NACIONAL.equals(input.tipoAtivo())
                || TipoAtivo.ACAO_INTERNACIONAL.equals(input.tipoAtivo())
                || TipoAtivo.FII.equals(input.tipoAtivo());
    }
}
