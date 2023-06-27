package br.com.carteira.dominio.cotacao;

import java.util.Optional;

public interface CotacaoGateway {

    void updateOrInsert(Cotacao cotacao);
    Optional<Cotacao> getByPapel(String papel);
}
