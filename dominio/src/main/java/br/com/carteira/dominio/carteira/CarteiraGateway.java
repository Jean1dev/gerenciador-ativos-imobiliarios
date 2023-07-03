package br.com.carteira.dominio.carteira;

import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;

public interface CarteiraGateway {

    Carteira salvar(Carteira carteira);

    void consolidar(Carteira carteira);

    Carteira buscarCarteiraPeloId(String id);

    boolean verificarSeJaExisteTickerNaCarteira(Carteira carteira, String ticker);

    void adicionarAtivoNaCarteira(Carteira carteira, AtivoSimplificado ativoSimplificado);
}
