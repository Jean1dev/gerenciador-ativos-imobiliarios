package br.com.carteira.infra.carteira;

import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;

public final class CarteiraFactory {

    public static Carteira umaCarteiraSimples() {
        Carteira carteira = new Carteira();
        carteira.setNome("para testes");
        return carteira;
    }

    public static CarteiraDocument carteiraDocument() {
        return new CarteiraDocument(null, "fake", null, "ref", 2);
    }
}
