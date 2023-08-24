package br.com.carteira.dominio.ativo.useCase.records;

import br.com.carteira.dominio.criterios.Criterio;

import java.util.List;

public record AtualizarAtivoInput(
        Double quantidade,
        Integer nota,
        String identificacao,
        List<Criterio> criterios,
        Double valor
) {
    public AtualizarAtivoInput(double quantidade, int nota, String idAtivo, List<Criterio> criterios) {
        this(quantidade, nota, idAtivo, criterios, null);
    }
}
