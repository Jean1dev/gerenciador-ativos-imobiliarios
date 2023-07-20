package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.criterios.Criterio;

import java.util.List;

public record AtivoSimplificado(
        TipoAtivo tipoAtivo,
        String papel,
        double quantidade,
        int nota,
        List<Criterio> criterios
) {
    public AtivoSimplificado(TipoAtivo tipoAtivo, String papel, double quantidade, Integer nota) {
        this(tipoAtivo, papel, quantidade, nota, null);
    }
}
