package br.com.carteira.dominio.ativo.useCase;

import br.com.carteira.dominio.TipoAtivo;
import br.com.carteira.dominio.ativo.AcaoNacional;
import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.exception.DominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("CalcularValorRecomendado")
class CalcularValorRecomendadoUseCaseTest {

    //    @Test
//    @DisplayName("deve calcular o valor recomendado corretamente")
//    public void deveCalcular() throws DominioException {
//        var meta = Meta.metasDoJeanluca();
//        var cdbXp = new AtivoQualquer(TipoAtivo.RENDA_FIXA,
//                "CDB XP",
//                0,
//                67670.56,
//                5,
//                0,
//                1);
//
//        var acoes = new AcaoNacional(
//                "TOTS3",
//                0,
//                14976.38,
//                5,
//                0,
//                1
//        );
//
//        var klbn11 = new AcaoNacional(
//                "KLBN11",
//                0,
//                22.26,
//                7,
//                0,
//                34
//        );
//
//        List<Ativo> ativoList = List.of(cdbXp, acoes);
//        new CalcularValorRecomendadoUseCase().calcular(klbn11, meta, ativoList);
//    }
    @Test
    @DisplayName("deve calcular o valor recomendado corretamente")
    public void deveCalcular() throws DominioException {
        AcaoNacional weg3 = new AcaoNacional(
                "WEG3",
                0,
                0,
                1,
                0,
                1
        );

        var useCase = new CalcularValorRecomendadoUseCase();
        assertEquals(100, useCase.calcular(weg3, Collections.emptySet()));

        var itub3 = new AcaoNacional(
                "ITUB3",
                0,
                0,
                9,
                0,
                1
        );

        assertEquals(90, useCase.calcular(itub3, Collections.singleton(weg3)));
        assertEquals(10, useCase.calcular(weg3, Collections.singleton(itub3)));

        HashSet<Ativo> ativos = new HashSet<>();
        var petr4 = new AcaoNacional(
                "PETR4",
                0,
                0,
                9,
                0,
                1
        );

        var flry3 = new AcaoNacional(
                "FLRY3",
                0,
                0,
                3,
                0,
                1
        );
        ativos.addAll(Arrays.asList(weg3, itub3, petr4));
        assertEquals(13.6, useCase.calcular(flry3, ativos));
    }

    private class AtivoQualquer extends Ativo {
        public AtivoQualquer(TipoAtivo tipoAtivo, String localAlocado, double percentualRecomendado, double valorAtual, Integer nota, double percentualTotal, double quantidade) throws DominioException {
            super(tipoAtivo, localAlocado, percentualRecomendado, valorAtual, nota, percentualTotal, quantidade);
        }
    }
}