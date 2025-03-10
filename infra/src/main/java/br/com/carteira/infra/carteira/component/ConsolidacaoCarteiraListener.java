package br.com.carteira.infra.carteira.component;

import br.com.carteira.dominio.ativo.AtivoComTicker;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.CalcularPorcentagemSobreOTotalUseCase;
import br.com.carteira.dominio.ativo.useCase.CalcularValorRecomendadoUseCase;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.crypto.CryptoAtivosMapping;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.integracoes.CryptoPricesApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.carteira.infra.utils.Utils.ehAcaoNacional;

@Component
@EnableAsync
public class ConsolidacaoCarteiraListener {
    private static final Logger log = LoggerFactory.getLogger(ConsolidacaoCarteiraListener.class);
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final AtivoDosUsuariosRepository ativoDosUsuariosRepository;
    private final CryptoPricesApi cryptoPricesApi;

    public ConsolidacaoCarteiraListener(
            AtivoComCotacaoRepository ativoComCotacaoRepository,
            AtivoDosUsuariosRepository ativoDosUsuariosRepository,
            CryptoPricesApi cryptoPricesApi
    ) {
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.ativoDosUsuariosRepository = ativoDosUsuariosRepository;
        this.cryptoPricesApi = cryptoPricesApi;
    }

    @EventListener
    @Async
    public void on(Carteira carteira) {
        log.info("consolidando carteira " + carteira.getNome());
        if (carteira.getAtivos().isEmpty()) {
            log.info("carteira vazia não é necessario consolidação");
            return;
        }

        consolidarAcoes(carteira);
    }

    private void consolidarAcoes(Carteira carteira) {
        final List<String> tickersList = carteira.getAtivosComTicker()
                .stream().map(AtivoComTicker::getTicker)
                .toList();

        log.info("Quantidade de ativos " + tickersList.size());
        consolidarCrypto(carteira);

        List<String> tickersJaMonitorados = new ArrayList<>();

        // TODO:: REFATORAR PARA FAZER TODAS AS OPERACOES EM LISTA
        ativoComCotacaoRepository.findAllByTickerIn(tickersList).forEach(ativoComCotacao -> {
            final var valorAtual = ativoComCotacao.getValor();
            log.info(String.format("att cotacao ativo %s para %s", ativoComCotacao.getTicker(), valorAtual));
            tickersJaMonitorados.add(ativoComCotacao.getTicker());

            ativoDosUsuariosRepository
                    .findByCarteiraRefAndTicker(carteira.getIdentificacao(), ativoComCotacao.getTicker())
                    .ifPresentOrElse(
                            ativoDosUsuarios -> {
                                calcularEAtualizarAtivoDoUsuario(ativoDosUsuarios, valorAtual, carteira);
                                ativoDosUsuarios.setImage(ativoComCotacao.getImage());
                                ativoDosUsuariosRepository.save(ativoDosUsuarios);
                            },
                            () -> {
                                var ativo = carteira.getAtivoByTicker(ativoComCotacao.getTicker());

                                ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                                        null,
                                        carteira.getIdentificacao(),
                                        ativo.getTipoAtivo(),
                                        ativo.getLocalAlocado(),
                                        ativo.getPercentualRecomendado(),
                                        valorAtual,
                                        ativo.getNota(),
                                        ativo.getPercentualTotal(),
                                        ativo.getQuantidade(),
                                        ativoComCotacao.getTicker(),
                                        ativoComCotacao.getImage(),
                                        null));
                            });
        });

        final var tickersQuePrecisamSerMonitorados = tickersList
                .stream()
                .filter(tick -> {
                    boolean contains = tickersJaMonitorados.contains(tick);
                    return !contains;
                })
                .toList();

        log.info(String.format("quantidade de novas ações a ser monitoradas %s", tickersQuePrecisamSerMonitorados.size()));
        ativoComCotacaoRepository.saveAll(tickersQuePrecisamSerMonitorados.stream()
                .map(ticker -> AtivoComCotacao.criarCotacao(ticker, discoverTipoAtivo(ticker)))
                .toList());
    }

    private TipoAtivo discoverTipoAtivo(String ticker) {
        if (ehAcaoNacional(ticker)) {
            return TipoAtivo.ACAO_NACIONAL;
        }

        return null;
    }

    private void consolidarCrypto(Carteira carteira) {
        carteira.getCryptos()
                .stream()
                .filter(crypto -> CryptoAtivosMapping.ifContainsGetName(crypto.getTicker()).isPresent())
                .forEach(crypto -> ativoDosUsuariosRepository.findByCarteiraRefAndTicker(carteira.getIdentificacao(), crypto.getTicker())
                        .ifPresent(ativoDosUsuarios -> cryptoPricesApi.get()
                                .stream()
                                .filter(cryptoPricesDto -> cryptoPricesDto.name().equals(crypto.getTicker()))
                                .findFirst()
                                .ifPresent(cryptoPricesDto -> {
                                    calcularEAtualizarAtivoDoUsuario(ativoDosUsuarios, cryptoPricesDto.price(), carteira);
                                    ativoDosUsuariosRepository.save(ativoDosUsuarios);
                                })));
    }

    private AtivoDosUsuarios calcularEAtualizarAtivoDoUsuario(AtivoDosUsuarios ativoDosUsuarios, double valorAtual, Carteira carteira) {
        ativoDosUsuarios.setValorAtual(valorAtual);

        var ativoComTicker = AtivoDosUsuarios.toAtivoComTicker(ativoDosUsuarios);
        var porcentagemSobreTotal = new CalcularPorcentagemSobreOTotalUseCase().calcular(ativoComTicker, carteira.getAtivos());
        var valorRecomendado = new CalcularValorRecomendadoUseCase().calcular(ativoComTicker, carteira.removeByTicker(ativoComTicker.getTicker()));

        ativoDosUsuarios.setPercentualTotal(porcentagemSobreTotal);
        ativoDosUsuarios.setValorRecomendado(valorRecomendado);
        return ativoDosUsuarios;
    }
}
