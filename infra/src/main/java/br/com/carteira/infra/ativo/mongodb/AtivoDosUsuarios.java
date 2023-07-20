package br.com.carteira.infra.ativo.mongodb;

import br.com.carteira.dominio.ativo.AtivoComTicker;
import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.criterios.Criterio;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    private double valorRecomendado;
    private String image;
    List<Criterio> criterios;

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
            String ticker,
            String image,
            List<Criterio> criterios) {
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
        this.image = image;
        this.criterios = criterios;
    }

    public static AtivoComTicker toAtivoComTicker(AtivoDosUsuarios ativoDosUsuarios) {
        return new AtivoComTicker(
                ativoDosUsuarios.getTicker(),
                ativoDosUsuarios.getTipoAtivo(),
                ativoDosUsuarios.getLocalAlocado(),
                ativoDosUsuarios.getPercentualRecomendado(),
                ativoDosUsuarios.getValorAtual(),
                ativoDosUsuarios.getNota(),
                ativoDosUsuarios.getPercentualTotal(),
                ativoDosUsuarios.getQuantidade()
        );
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCriterios(List<Criterio> criterios) {
        this.criterios = criterios;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public void setPercentualTotal(double percentualTotal) {
        this.percentualTotal = percentualTotal;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public double getValorRecomendado() {
        return valorRecomendado;
    }

    public void setValorRecomendado(double valorRecomendado) {
        this.valorRecomendado = valorRecomendado;
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

    public String getImage() {
        return image;
    }

    public List<Criterio> getCriterios() {
        return criterios;
    }
}
