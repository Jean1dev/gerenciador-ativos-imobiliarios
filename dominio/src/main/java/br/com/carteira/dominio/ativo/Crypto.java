package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.exception.DominioException;

public class Crypto extends Ativo {
    private String ticker;

    public Crypto(TipoAtivo tipoAtivo, String localAlocado, double percentualRecomendado, double valorAtual, int nota, double percentualTotal, double quantidade) throws DominioException {
        super(tipoAtivo, localAlocado, percentualRecomendado, valorAtual, nota, percentualTotal, quantidade);
    }

    public static Crypto fromParent(Ativo ativo, String symbol) {
        Crypto crypto = new Crypto(
                ativo.getTipoAtivo(),
                ativo.getLocalAlocado(),
                ativo.getPercentualRecomendado(),
                ativo.getValorAtual(),
                ativo.getNota(),
                ativo.getPercentualTotal(),
                ativo.getQuantidade()
        );
        crypto.ticker = symbol;
        return crypto;
    }

    public String getTicker() {
        return ticker;
    }
}
