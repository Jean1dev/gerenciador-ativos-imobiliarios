package br.com.carteira.infra.ativo.mongodb;

import br.com.carteira.dominio.ativo.TipoAtivo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ativo_dos_usuarios")
public class AtivoDosUsuarios {
    @Id
    private String id;
    private String carteiraRef;
    private TipoAtivo tipoAtivo;
    private String localAlocado;
    private double percentualRecomendado;
    private double valorAtual;
    private int nota;
    private double percentualTotal;
    private double quantidade;
    private String ticker;

    public AtivoDosUsuarios(
            String id,
            String carteiraRef,
            TipoAtivo tipoAtivo,
            String localAlocado,
            double percentualRecomendado,
            double valorAtual,
            int nota,
            double percentualTotal,
            double quantidade,
            String ticker
    ) {
        this.id = id;
        this.carteiraRef = carteiraRef;
        this.tipoAtivo = tipoAtivo;
        this.localAlocado = localAlocado;
        this.percentualRecomendado = percentualRecomendado;
        this.valorAtual = valorAtual;
        this.nota = nota;
        this.percentualTotal = percentualTotal;
        this.quantidade = quantidade;
        this.ticker = ticker;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public String getId() {
        return id;
    }

    public String getCarteiraRef() {
        return carteiraRef;
    }

    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }

    public String getLocalAlocado() {
        return localAlocado;
    }

    public double getPercentualRecomendado() {
        return percentualRecomendado;
    }

    public double getValorAtual() {
        return valorAtual;
    }

    public int getNota() {
        return nota;
    }

    public double getPercentualTotal() {
        return percentualTotal;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public String getTicker() {
        return ticker;
    }
}
