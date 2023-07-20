package br.com.carteira.dominio.ativo.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.criterios.Criterio;

import java.util.List;

public record AdicionarAtivoInput(
        TipoAtivo tipoAtivo,
        Integer nota,
        Double valorAtual,
        String localAlocado,
        String tipoAlocacao,
        Double quantidade,
        String nome,
        String identificacaoCarteira,
        List<Criterio> criterios
) {
}
