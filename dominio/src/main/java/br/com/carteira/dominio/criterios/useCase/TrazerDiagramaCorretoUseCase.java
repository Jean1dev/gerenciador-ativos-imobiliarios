package br.com.carteira.dominio.criterios.useCase;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.criterios.AvaliacaoFundosImobiliarios;
import br.com.carteira.dominio.criterios.Criterio;
import br.com.carteira.dominio.criterios.DiagramaDoCerrado;

import java.util.Collections;
import java.util.List;

public class TrazerDiagramaCorretoUseCase {

    public List<Criterio> padrao(TipoAtivo tipoAtivo) {
        if (TipoAtivo.ACAO_NACIONAL.equals(tipoAtivo)) {
            return new DiagramaDoCerrado().getCriterios();
        }

        if (TipoAtivo.FII.equals(tipoAtivo)) {
            return new AvaliacaoFundosImobiliarios().getCriterios();
        }

        return Collections.emptyList();
    }

}
