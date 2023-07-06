package br.com.carteira.dominio.ativo.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;

public record AdicionarAtivoInput(
        TipoAtivo tipoAtivo,
        Integer nota,
        Double valorAtual,
        String localAlocado,
        String tipoAlocacao,
        Double quantidade,
        String nome,
        String identificacaoCarteira
) {
}
