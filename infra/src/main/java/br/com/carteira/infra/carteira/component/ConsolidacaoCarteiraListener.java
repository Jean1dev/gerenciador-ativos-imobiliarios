package br.com.carteira.infra.carteira.component;

import br.com.carteira.dominio.ativo.AcaoInternacional;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EnableAsync
public class ConsolidacaoCarteiraListener {
    private static final Logger log = LoggerFactory.getLogger(ConsolidacaoCarteiraListener.class);
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    public ConsolidacaoCarteiraListener(
            AtivoComCotacaoRepository ativoComCotacaoRepository,
            AtivoDosUsuariosRepository ativoDosUsuariosRepository
    ) {
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.ativoDosUsuariosRepository = ativoDosUsuariosRepository;
    }

    @EventListener
    @Async
    public void on(Carteira carteira) {
        log.info("consolidando carteira " + carteira.getNome());
        if (carteira.getAtivos().isEmpty()) {
            log.info("carteira vazia não é necessario consolidação");
            return;
        }

        consolidarAcoes(carteira, false);
        consolidarAcoes(carteira, true);
    }

    private void consolidarAcoes(Carteira carteira, boolean internacional) {
        final List<String> tickersList;
        if (internacional) {
            log.info("consolidando internacioal");
            tickersList = carteira.getAcoesInternacionais()
                    .stream()
                    .map(AcaoInternacional::getTicker).toList();
        } else {
            log.info("consolidando nacional");
            tickersList = carteira.getAcoesNacionais()
                    .stream()
                    .map(AcaoNacional::getTicker).toList();
        }

        List<String> tickersJaMonitorados = new ArrayList<>();
        ativoComCotacaoRepository.findAllByTickerIn(tickersList).forEach(ativoComCotacao -> {
            final var valorAtual = ativoComCotacao.getValor();
            log.info(String.format("att cotacao ativo %s para %s", ativoComCotacao.getTicker(), valorAtual));
            tickersJaMonitorados.add(ativoComCotacao.getTicker());

            ativoDosUsuariosRepository
                    .findByCarteiraRefAndTicker(carteira.getIdentificacao(), ativoComCotacao.getTicker())
                    .ifPresentOrElse(ativoDosUsuarios -> {
                        ativoDosUsuarios.setValorAtual(valorAtual);
                        ativoDosUsuariosRepository.save(ativoDosUsuarios);
                    }, () -> {
                        Ativo acao;
                        if (internacional) {
                            acao = carteira.getAcaoInternacionalByTicker(ativoComCotacao.getTicker());
                        } else {
                            acao = carteira.getAcaoNacionalByTicker(ativoComCotacao.getTicker());
                        }

                        ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                                null,
                                carteira.getIdentificacao(),
                                acao.getTipoAtivo(),
                                acao.getLocalAlocado(),
                                acao.getPercentualRecomendado(),
                                valorAtual,
                                acao.getNota(),
                                acao.getPercentualTotal(),
                                acao.getQuantidade(),
                                ativoComCotacao.getTicker()
                        ));
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
                .map(s -> {
                    if (internacional)
                        return AtivoComCotacao.criarAcaoInternacionalFromTicker(s);

                    return AtivoComCotacao.criarAcaoNacionalFromTicker(s);
                })
                .toList());
    }
}
