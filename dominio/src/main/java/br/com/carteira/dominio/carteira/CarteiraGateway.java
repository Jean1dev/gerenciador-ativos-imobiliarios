package br.com.carteira.dominio.carteira;

public interface CarteiraGateway {

    Carteira salvar(Carteira carteira);

    void consolidar(Carteira carteira);

    Carteira buscarCarteiraPeloId(String id);
}
