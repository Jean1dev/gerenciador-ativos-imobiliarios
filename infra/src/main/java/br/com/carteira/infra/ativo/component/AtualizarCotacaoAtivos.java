package br.com.carteira.infra.ativo.component;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.admin.service.AtivosComProblemasService;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.records.EnviarEmailPayload;
import br.com.carteira.infra.ativo.records.ResultadoAtualizacaoAtivo;
import br.com.carteira.infra.ativo.records.VariacaoAtivo;
import br.com.carteira.infra.ativo.service.CalcularVariacaoAtivoDuranteTimeBox;
import br.com.carteira.infra.ativo.service.VariacaoAtivosService;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@EnableAsync
public class AtualizarCotacaoAtivos {
    private static final Logger log = LoggerFactory.getLogger(AtualizarCotacaoAtivos.class);

    private final BMFBovespa bmfBovespa;
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final ObjectMapper mapper;
    private final AtivosComProblemasService ativosComProblemasService;
    private final VariacaoAtivosService variacaoAtivosService;

    public AtualizarCotacaoAtivos(BMFBovespa bmfBovespa,
                                  AtivoComCotacaoRepository ativoComCotacaoRepository,
                                  ObjectMapper mapper,
                                  AtivosComProblemasService ativosComProblemasService,
                                  VariacaoAtivosService variacaoAtivosService) {
        this.bmfBovespa = bmfBovespa;
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.mapper = mapper;
        this.ativosComProblemasService = ativosComProblemasService;
        this.variacaoAtivosService = variacaoAtivosService;
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
                    log.info(ativoComCotacao.getTicker());
                    var resultadoAtualizacaoAtivo = atualizarCotacao(ativoComCotacao);
                    asyncRegistrarVariacao(resultadoAtualizacaoAtivo);
                    return resultadoAtualizacaoAtivo.mensagem();
                }).collect(Collectors.joining(System.lineSeparator()));

        evidenciarResultado(collected + bmfBovespa.getErrorList());
    }

    private void asyncRegistrarVariacao(ResultadoAtualizacaoAtivo resultadoAtualizacaoAtivo) {
        if (Objects.isNull(resultadoAtualizacaoAtivo.cotacaoNova()))
            return;

        var virtualThread = Thread.startVirtualThread(() -> {
            var variacapDePreco = CalcularVariacaoAtivoDuranteTimeBox.calc(
                    resultadoAtualizacaoAtivo.cotacaoAntiga(),
                    resultadoAtualizacaoAtivo.dataCriacao(),
                    resultadoAtualizacaoAtivo.cotacaoNova(),
                    resultadoAtualizacaoAtivo.dataUltimaAtualizacao()
            );

            var ativoVariado = new VariacaoAtivo(
                    resultadoAtualizacaoAtivo.ticker(),
                    variacapDePreco.diferencaDeDias(),
                    variacapDePreco.percentualVariacao(),
                    resultadoAtualizacaoAtivo.dataCriacao().toLocalDate(),
                    resultadoAtualizacaoAtivo.dataUltimaAtualizacao().toLocalDate()
            );
            log.info("salvando variacao de ativo {}", ativoVariado);
            variacaoAtivosService.salvarVariacaoAtivo(ativoVariado);
        });

        log.info("virtual thread criada", virtualThread.threadId());
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

    private ResultadoAtualizacaoAtivo atualizarCotacao(AtivoComCotacao ativoComCotacao) {
        var searchTicker = getTickerParaPesquisa(ativoComCotacao);
        var cotacao = bmfBovespa.getCotacao(searchTicker);
        if (cotacao != null) {

            if (cotacao.valor().intValue() == 0) {
                return ResultadoAtualizacaoAtivo.from(ativoComCotacao, "Nao atualizado valor para 0");
            }

            var message = String.format("atualizado %s para %s", ativoComCotacao.getTicker(), cotacao.valor());
            log.info(message);
            ativoComCotacao.atualizarValor(cotacao.valor());
            ativoComCotacaoRepository.save(ativoComCotacao);
            return ResultadoAtualizacaoAtivo.from(ativoComCotacao, message, cotacao.valor());
        }

        ativosComProblemasService.evidenciar(ativoComCotacao.getTicker());
        return ResultadoAtualizacaoAtivo.from(ativoComCotacao, "Nao foi possivel atualizar " + ativoComCotacao.getTicker());
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
