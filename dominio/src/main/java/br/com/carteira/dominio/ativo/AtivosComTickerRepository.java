package br.com.carteira.dominio.ativo;

import java.util.Optional;

public interface AtivosComTickerRepository {

    Optional<Ativo> buscarPeloTicker(String ticker);
}
