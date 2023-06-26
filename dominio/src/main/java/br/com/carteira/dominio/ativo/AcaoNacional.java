package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
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

    public static AcaoNacional fromSimplificado(AtivoSimplificado ativoSimplificado) throws DominioException {
        return new AcaoNacional(
                ativoSimplificado.papel().toUpperCase(),
                0,
                0,
                ativoSimplificado.nota(),
                0,
                ativoSimplificado.quantidade()
        );
    }

    public String getTicker() {
        return ticker;
    }
}
