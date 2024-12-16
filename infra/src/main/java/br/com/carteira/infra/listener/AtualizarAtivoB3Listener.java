package br.com.carteira.infra.listener;

import br.com.carteira.infra.admin.service.AtivosComProblemasService;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.records.ResultadoAtualizacaoAtivo;
import br.com.carteira.infra.ativo.records.VariacaoAtivo;
import br.com.carteira.infra.ativo.service.CalcularVariacaoAtivoDuranteTimeBox;
import br.com.carteira.infra.ativo.service.VariacaoAtivosService;
import br.com.carteira.infra.integracoes.BMFBovespa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static br.com.carteira.infra.utils.Utils.getTickerParaPesquisa;

@Component
public class AtualizarAtivoB3Listener {

    private static final Logger log = LoggerFactory.getLogger(AtualizarAtivoB3Listener.class);
    private final BMFBovespa bmfBovespa;
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final AtivosComProblemasService ativosComProblemasService;
    private final VariacaoAtivosService variacaoAtivosService;

    public AtualizarAtivoB3Listener(
            BMFBovespa bmfBovespa,
            AtivoComCotacaoRepository ativoComCotacaoRepository,
            AtivosComProblemasService ativosComProblemasService,
            VariacaoAtivosService variacaoAtivosService) {
        this.bmfBovespa = bmfBovespa;
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.ativosComProblemasService = ativosComProblemasService;
        this.variacaoAtivosService = variacaoAtivosService;
    }

    @EventListener
    public void on(AtivoComCotacao ativoComCotacao) {
        var resultadoAtualizacaoAtivo = atualizarCotacaoCorretaPorTipo(ativoComCotacao);
        asyncRegistrarVariacao(resultadoAtualizacaoAtivo);
    }

    private ResultadoAtualizacaoAtivo atualizarCotacaoCorretaPorTipo(AtivoComCotacao ativoComCotacao) {
        return switch (ativoComCotacao.getTipoAtivo()) {
            case ACAO_NACIONAL, ACAO_INTERNACIONAL -> atualizarCotacao(ativoComCotacao);
            default -> ResultadoAtualizacaoAtivo.from(ativoComCotacao, "Tipo de ativo não suportado");
        };
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


    private ResultadoAtualizacaoAtivo atualizarCotacao(AtivoComCotacao ativoComCotacao) {
        var searchTicker = getTickerParaPesquisa(ativoComCotacao);
        var cotacaoAntiga = ativoComCotacao.getValor();
        var cotacao = bmfBovespa.getCotacao(searchTicker);
        if (!cotacao.hasError()) {

            if (cotacao.valor().intValue() == 0) {
                return ResultadoAtualizacaoAtivo.from(ativoComCotacao, "Nao atualizado valor para 0");
            }

            var message = String.format("atualizado %s para %s", ativoComCotacao.getTicker(), cotacao.valor());
            log.info(message);
            ativoComCotacao.atualizarValor(cotacao.valor());
            ativoComCotacaoRepository.save(ativoComCotacao);
            return ResultadoAtualizacaoAtivo.from(ativoComCotacao, message, cotacaoAntiga);
        }

        ativosComProblemasService.evidenciar(ativoComCotacao.getTicker(), cotacao.errorMessage());
        return ResultadoAtualizacaoAtivo.from(ativoComCotacao, "Nao foi possivel atualizar " + ativoComCotacao.getTicker());
    }
}
