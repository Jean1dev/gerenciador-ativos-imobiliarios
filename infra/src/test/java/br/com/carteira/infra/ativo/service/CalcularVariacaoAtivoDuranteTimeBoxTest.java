package br.com.carteira.infra.ativo.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalcularVariacaoAtivoDuranteTimeBoxTest {

    @Test
    void shoulBeEvictedInifinityResult() {
        var variacapDePreco = CalcularVariacaoAtivoDuranteTimeBox.calc(0.0, LocalDateTime.now(), 10.0, LocalDateTime.now());
        assertEquals(0.0, variacapDePreco.percentualVariacao());
    }
}