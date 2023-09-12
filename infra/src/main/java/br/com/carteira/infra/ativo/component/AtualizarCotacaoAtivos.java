package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.integracoes.BMFBovespa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableAsync
public class AtualizarCotacaoAtivos {
    private static final Logger log = LoggerFactory.getLogger(AtualizarCotacaoAtivos.class);

    private final BMFBovespa bmfBovespa;
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;

    public AtualizarCotacaoAtivos(BMFBovespa bmfBovespa, AtivoComCotacaoRepository ativoComCotacaoRepository) {
        this.bmfBovespa = bmfBovespa;
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
    }

    @Async
    public void run() {
        log.info("iniciando processo de atualizacao de ativos");
        ativoComCotacaoRepository.findAll()
                .stream()
                .filter(this::deveAtualizar)
                .parallel()
                .forEach(ativoComCotacao -> {
                    var ticker = getTickerParaPesquisa(ativoComCotacao);
                    log.info(ticker);
                    atualizarCotacao(ticker, ativoComCotacao);
                });
    }

    private void atualizarCotacao(String ticker, AtivoComCotacao ativoComCotacao) {
        var cotacao = bmfBovespa.getCotacao(ticker);
        if (cotacao != null) {
            log.info(String.format("atualizando %s para %s", ticker, cotacao.valor()));
            ativoComCotacao.atualizarValor(cotacao.valor());
            ativoComCotacaoRepository.save(ativoComCotacao);
        }
    }

    private boolean deveAtualizar(AtivoComCotacao ativoComCotacao) {
        if (ativoComCotacao.getValor() == 0) {
            return true;
        }

        return LocalDate.now().isAfter(ativoComCotacao.getUltimaAtualizacao().toLocalDate());
    }

    private String getTickerParaPesquisa(AtivoComCotacao ativoComCotacao) {
        if (TipoAtivo.ACAO_NACIONAL.equals(ativoComCotacao.getTipoAtivo())) {
            return ativoComCotacao.getTicker() + ".SAO";
        }

        return ativoComCotacao.getTicker();
    }
}
