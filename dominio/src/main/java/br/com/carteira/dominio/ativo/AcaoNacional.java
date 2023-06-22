package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.TipoAtivo;
import br.com.carteira.dominio.exception.DominioException;

import java.util.Objects;

public class AcaoNacional extends Ativo {
    private String ticker;

    public AcaoNacional(
            String ticker,
            double percentualRecomendado,
            double valorAtual,
            Integer nota,
            double percentualTotal,
            double quantidade
    ) throws DominioException {
        super(TipoAtivo.ACAO_NACIONAL, "B3", percentualRecomendado, valorAtual, nota, percentualTotal, quantidade);
        this.ticker = Objects.requireNonNull(ticker, "O ticker não pode ser nullo");
    }

    public String getTicker() {
        return ticker;
    }
}
