package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.exception.DominioException;

public class AtivoComTicker extends Ativo {
    private String ticker;

    public AtivoComTicker(
            String ticker,
            TipoAtivo tipoAtivo,
            String localAlocado,
            double percentualRecomendado,
            double valorAtual,
            int nota,
            double percentualTotal,
            double quantidade
    ) throws DominioException {
        super(tipoAtivo, localAlocado, percentualRecomendado, valorAtual, nota, percentualTotal, quantidade);
        this.ticker = ticker.toUpperCase();
    }

    public static AtivoComTicker fromSimplificado(AtivoSimplificado ativoSimplificado) throws DominioException {
        return new AtivoComTicker(
                ativoSimplificado.papel().toUpperCase(),
                ativoSimplificado.tipoAtivo(),
                ativoSimplificado.papel(),
                0,
                0,
                ativoSimplificado.nota(),
                0,
                ativoSimplificado.quantidade()
        );
    }

    public static AtivoComTicker fromParent(String ticker, Ativo parent) {
        return new AtivoComTicker(
                ticker,
                parent.getTipoAtivo(),
                parent.getLocalAlocado(),
                parent.getPercentualRecomendado(),
                parent.getValorAtual(),
                parent.getNota(),
                parent.getPercentualTotal(),
                parent.getQuantidade()
        );
    }

    public String getTicker() {
        return ticker;
    }

    @Override
    public String getLocalAlocado() {
        return ticker;
    }
}
