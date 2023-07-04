package br.com.carteira.infra.ativo.component;

import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.integracoes.BMFBovespa;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void onStartUp() {
        run();
    }

    @Async
    public void run() {
        log.info("iniciando processo de atualizacao de ativos");
        var now = LocalDate.now();
        ativoComCotacaoRepository.findAll().stream().filter(ativoComCotacao -> {
            if (now.isAfter(ativoComCotacao.getUltimaAtualizacao().toLocalDate())) {
                return true;
            }

            return false;
        }).forEach(ativoComCotacao -> {
            var cotacao = bmfBovespa.getCotacao(ativoComCotacao.getTicker() + ".SAO");
            if (cotacao != null) {
                log.info(String.format("atualizando %s para %s", ativoComCotacao.getTicker(), cotacao.valor()));
                ativoComCotacao.atualizarValor(cotacao.valor());
                ativoComCotacaoRepository.save(ativoComCotacao);
            }
        });
    }
}
