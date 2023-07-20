package br.com.carteira.dominio.ativo.useCase.records;

import br.com.carteira.dominio.criterios.Criterio;

import java.util.List;

public record AtualizarAtivoInput(
        Double quantidade,
        Integer nota,
        String identificacao,
        List<Criterio> criterios
) {
}
