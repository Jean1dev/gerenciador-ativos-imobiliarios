package br.com.carteira.infra.ativo.mongodb;

import br.com.carteira.dominio.ativo.AcaoInternacional;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
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
    private String image;

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

    public static Ativo fromDocument(AtivoComCotacao ativoComCotacao) {
        if (TipoAtivo.ACAO_INTERNACIONAL.equals(ativoComCotacao.tipoAtivo)) {
            return new AcaoInternacional(
                    ativoComCotacao.getTicker(),
                    0,
                    ativoComCotacao.getValor(),
                    0,
                    0,
                    0
            );
        }

        if (TipoAtivo.ACAO_NACIONAL.equals(ativoComCotacao.tipoAtivo)) {
            return new AcaoNacional(
                    ativoComCotacao.getTicker(),
                    0,
                    ativoComCotacao.getValor(),
                    0,
                    0,
                    0
            );
        }
        return null;
    }

    public static AtivoComCotacao criarCotacao(String ticker, TipoAtivo tipoAtivo) {
        return new AtivoComCotacao(
                null,
                ticker,
                tipoAtivo,
                LocalDateTime.now(),
                0
        );
    }

    public void atualizarValor(Double valor) {
        this.valor = valor;
        this.ultimaAtualizacao = LocalDateTime.now().plusDays(7);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        ultimaAtualizacao = LocalDateTime.now();
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
