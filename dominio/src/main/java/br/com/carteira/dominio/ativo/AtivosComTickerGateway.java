package br.com.carteira.dominio.ativo;

import java.util.Optional;

public interface AtivosComTickerGateway {

    Optional<Ativo> buscarPeloTicker(String ticker);

    void adicionarParaMonitoramento(String ticker, TipoAtivo tipoAtivo);

    void atualizarAtivo(String ativoId, Integer nota, Double quantidade);
}
