package br.com.carteira.infra.carteira;

import br.com.carteira.dominio.carteira.Carteira;

public final class CarteiraFactory {

    public static Carteira umaCarteiraSimples() {
        Carteira carteira = new Carteira();
        carteira.setNome("para testes");
        return carteira;
    }
}
