package br.com.carteira.infra.avaliacao.fundamentalista.api;

import java.util.List;

public record RelatorioFundamentalistaDTO(
        String geradoEm,
        List<AvaliacaoAtivoDTO> ativos
) {
}
