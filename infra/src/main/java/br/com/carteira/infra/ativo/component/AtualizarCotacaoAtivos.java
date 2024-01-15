package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.admin.service.AtivosComProblemasService;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.integracoes.BMFBovespa;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class AtualizarCotacaoAtivos {
    private static final Logger log = LoggerFactory.getLogger(AtualizarCotacaoAtivos.class);

    private final BMFBovespa bmfBovespa;
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final ObjectMapper mapper;
    private final AtivosComProblemasService ativosComProblemasService;

    public AtualizarCotacaoAtivos(BMFBovespa bmfBovespa,
                                  AtivoComCotacaoRepository ativoComCotacaoRepository,
                                  ObjectMapper mapper,
                                  AtivosComProblemasService ativosComProblemasService) {
        this.bmfBovespa = bmfBovespa;
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.mapper = mapper;
        this.ativosComProblemasService = ativosComProblemasService;
    }

    @Async
    public void run() {
        log.info("iniciando processo de atualizacao de ativos");
        List<TipoAtivo> tipos = List.of(TipoAtivo.ACAO_INTERNACIONAL, TipoAtivo.ACAO_NACIONAL);
        String collected = ativoComCotacaoRepository.findAllByTipoAtivoIn(tipos)
                .stream()
                .filter(this::deveAtualizar)
                .parallel()
                .map(ativoComCotacao -> {
                    var ticker = getTickerParaPesquisa(ativoComCotacao);
                    log.info(ticker);
                    return atualizarCotacao(ticker, ativoComCotacao);
                }).collect(Collectors.joining(System.lineSeparator()));

        evidenciarResultado(collected + bmfBovespa.getErrorList());
    }

    private void evidenciarResultado(String collected) {
        if (collected.isBlank())
            return;
        var url = "https://communication-service-4f4f57e0a956.herokuapp.com/email";
        var restTemplate = new RestTemplate();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            var jsonBody = mapper.writeValueAsString(new EnviarEmailPayload("jeanlucafp@gmail.com", "Resultado atualizacao ativos", collected));
            var requestEntity = new HttpEntity<>(jsonBody, headers);

            var responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

            log.info(String.format("Server response %s", responseEntity.getBody()));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
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

        ativosComProblemasService.evidenciar(ticker);
        return "Nao foi possivel atualizar " + ticker;
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
