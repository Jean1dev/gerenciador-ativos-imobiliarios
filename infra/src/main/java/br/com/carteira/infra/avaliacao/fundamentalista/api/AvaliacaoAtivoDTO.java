package br.com.carteira.infra.avaliacao.fundamentalista.api;

import br.com.carteira.dominio.ativo.TipoAtivo;

import java.util.List;

public record AvaliacaoAtivoDTO(
        String codigo,
        String nome,
        TipoAtivo tipoAtivo,
        double nota,
        List<RespostaCriterioDTO> respostas,
        String dataAvaliacao,
        List<String> fontesUtilizadas
) {
}
