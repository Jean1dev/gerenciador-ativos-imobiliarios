package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.integracoes.BMFBovespa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.stream.Collectors;

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
        String collected = ativoComCotacaoRepository.findAll()
                .stream()
                .filter(this::deveAtualizar)
                .parallel()
                .map(ativoComCotacao -> {
                    var ticker = getTickerParaPesquisa(ativoComCotacao);
                    log.info(ticker);
                    return atualizarCotacao(ticker, ativoComCotacao);
                }).collect(Collectors.joining());

        evidenciarResultado(collected);
    }

    private void evidenciarResultado(String collected) {
        if (collected.isBlank())
            return;
        var url = "https://communication-service-4f4f57e0a956.herokuapp.com/email";
        var restTemplate = new RestTemplate();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = String.format("{\"to\": \"jeanlucafp@gmail.com\", \"subject\": \"Resultado atualizacao ativos\", \"message\": \"%s\"}", collected);
        var requestEntity = new HttpEntity<>(jsonBody, headers);

        var responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        log.info(String.format("Server response %s", responseEntity.getBody()));
    }

    private String atualizarCotacao(String ticker, AtivoComCotacao ativoComCotacao) {
        var cotacao = bmfBovespa.getCotacao(ticker);
        if (cotacao != null) {
            var message = String.format("atualizado %s para %s", ticker, cotacao.valor());
            log.info(message);
            ativoComCotacao.atualizarValor(cotacao.valor());
            ativoComCotacaoRepository.save(ativoComCotacao);
            return message;
        }

        return "Nao foi possivel atualizar " + cotacao;
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
