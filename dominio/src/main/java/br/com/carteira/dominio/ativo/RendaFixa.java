package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.exception.DominioException;

public class RendaFixa extends Ativo {
    public RendaFixa(TipoAtivo tipoAtivo, String localAlocado, double percentualRecomendado, double valorAtual, int nota, double percentualTotal, double quantidade) throws DominioException {
        super(tipoAtivo, localAlocado, percentualRecomendado, valorAtual, nota, percentualTotal, quantidade);
    }
}
