package br.com.carteira.dominio.carteira.useCase.records;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.criterios.Criterio;

import java.util.List;

public record AtivoSimplificado(
        TipoAtivo tipoAtivo,
        String papel,
        double quantidade,
        int nota,
        List<Criterio> criterios,
        double valor
) {
    public AtivoSimplificado(TipoAtivo tipoAtivo, String papel, double quantidade, Integer nota) {
        this(tipoAtivo, papel, quantidade, nota, null, 0.0);
    }

    public AtivoSimplificado(TipoAtivo tipoAtivo, String upperCase, Double quantidade, Integer nota, List<Criterio> criterios) {
        this(tipoAtivo, upperCase, quantidade, nota, criterios, 0.0);
    }
}
