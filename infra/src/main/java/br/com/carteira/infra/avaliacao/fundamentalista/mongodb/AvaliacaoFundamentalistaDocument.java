package br.com.carteira.infra.avaliacao.fundamentalista.mongodb;

import br.com.carteira.dominio.ativo.TipoAtivo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("avaliacao_fundamentalista")
public class AvaliacaoFundamentalistaDocument {
    @Id
    private String id;
    private String codigo;
    private String nome;
    private TipoAtivo tipoAtivo;
    private double nota;
    private Instant dataAvaliacao;
    private List<String> fontesUtilizadas;
    private List<RespostaCriterioDocument> respostas;
    private Instant geradoEm;

    public AvaliacaoFundamentalistaDocument() {
    }

    public AvaliacaoFundamentalistaDocument(
            String id,
            String codigo,
            String nome,
            TipoAtivo tipoAtivo,
            double nota,
            Instant dataAvaliacao,
            List<String> fontesUtilizadas,
            List<RespostaCriterioDocument> respostas,
            Instant geradoEm
    ) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.tipoAtivo = tipoAtivo;
        this.nota = nota;
        this.dataAvaliacao = dataAvaliacao;
        this.fontesUtilizadas = fontesUtilizadas;
        this.respostas = respostas;
        this.geradoEm = geradoEm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }

    public void setTipoAtivo(TipoAtivo tipoAtivo) {
        this.tipoAtivo = tipoAtivo;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public Instant getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Instant dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public List<String> getFontesUtilizadas() {
        return fontesUtilizadas;
    }

    public void setFontesUtilizadas(List<String> fontesUtilizadas) {
        this.fontesUtilizadas = fontesUtilizadas;
    }

    public List<RespostaCriterioDocument> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<RespostaCriterioDocument> respostas) {
        this.respostas = respostas;
    }

    public Instant getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Instant geradoEm) {
        this.geradoEm = geradoEm;
    }
}
