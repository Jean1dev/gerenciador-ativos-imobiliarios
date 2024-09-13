package br.com.carteira.infra.ativo.records;

import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;

import java.time.LocalDateTime;

public record ResultadoAtualizacaoAtivo(
        String ticker,
        Double cotacaoAntiga,
        Double cotacaoNova,
        String mensagem,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAtualizacao
) {
    public static ResultadoAtualizacaoAtivo from(AtivoComCotacao ativoComCotacao, String mensagem) {
        return new ResultadoAtualizacaoAtivo(
                ativoComCotacao.getTicker(),
                ativoComCotacao.getValor(),
                null,
                mensagem,
                LocalDateTime.now(),
                ativoComCotacao.getUltimaAtualizacao()
        );
    }

    public static ResultadoAtualizacaoAtivo from(AtivoComCotacao ativoComCotacao, String mensagem, Double cotacaoAntiga) {
        return new ResultadoAtualizacaoAtivo(
                ativoComCotacao.getTicker(),
                cotacaoAntiga,
                ativoComCotacao.getValor(),
                mensagem,
                LocalDateTime.now(),
                ativoComCotacao.getUltimaAtualizacao()
        );
    }
}
