package br.com.carteira.dominio.ativo;

import br.com.carteira.dominio.ativo.useCase.records.AtualizarAtivoInput;

import java.util.Optional;

public interface AtivosComTickerGateway {

    Optional<Ativo> buscarPeloTicker(String ticker);

    void adicionarParaMonitoramento(String ticker, TipoAtivo tipoAtivo);

    void atualizarAtivo(AtualizarAtivoInput input);
}
