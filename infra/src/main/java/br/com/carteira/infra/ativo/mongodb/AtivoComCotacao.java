package br.com.carteira.infra.ativo.mongodb;

import br.com.carteira.dominio.ativo.TipoAtivo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("ativo_com_cotacao")
public class AtivoComCotacao {
    @Id
    private String id;
    private String ticker;
    private TipoAtivo tipoAtivo;
    private LocalDateTime ultimaAtualizacao;
    private double valor;

    public AtivoComCotacao(
            String id,
            String ticker,
            TipoAtivo tipoAtivo,
            LocalDateTime ultimaAtualizacao,
            double valor
    ) {
        this.id = id;
        this.ticker = ticker;
        this.tipoAtivo = tipoAtivo;
        this.ultimaAtualizacao = ultimaAtualizacao;
        this.valor = valor;
    }

    public static AtivoComCotacao criarAcaoNacionalFromTicker(String ticker) {
        return criarCotacao(ticker, TipoAtivo.ACAO_NACIONAL);
    }

    public static AtivoComCotacao criarAcaoInternacionalFromTicker(String ticker) {
        return criarCotacao(ticker, TipoAtivo.ACAO_INTERNACIONAL);
    }

    private static AtivoComCotacao criarCotacao(String ticker, TipoAtivo tipoAtivo) {
        return new AtivoComCotacao(
                null,
                ticker,
                tipoAtivo,
                LocalDateTime.now(),
                0
        );
    }

    public double getValor() {
        return valor;
    }

    public String getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }
}
