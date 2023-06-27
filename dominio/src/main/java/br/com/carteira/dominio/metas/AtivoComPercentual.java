package br.com.carteira.dominio.metas;

import br.com.carteira.dominio.ativo.TipoAtivo;

public class AtivoComPercentual {
    private double percentual;
    private TipoAtivo tipoAtivo;

    public AtivoComPercentual(double percentual, TipoAtivo tipoAtivo) {
        this.percentual = percentual;
        this.tipoAtivo = tipoAtivo;
    }

    public double getPercentual() {
        return percentual;
    }

    public TipoAtivo getTipoAtivo() {
        return tipoAtivo;
    }
}
