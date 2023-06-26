package br.com.carteira.dominio.ativo;

import java.util.Optional;

public interface AtivosComTickerGateway {

    Optional<Ativo> buscarPeloTicker(String ticker);
}
