package br.com.carteira.infra.carteira.api.presenters;

import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class CarteiraDocumentPresent implements Serializable {
    private final String id;
    private final String nome;
    private final Integer quantidadeAtivos;

    private CarteiraDocumentPresent(String id, String nome, Integer quantidadeAtivos) {
        this.id = id;
        this.nome = nome;
        this.quantidadeAtivos = quantidadeAtivos;
    }

    public static CarteiraDocumentPresent present(CarteiraDocument document) {
        return new CarteiraDocumentPresent(
                document.getId(),
                document.getNome(),
                document.getQuantidadeAtivos());
    }

    public static List<CarteiraDocumentPresent> present(final List<CarteiraDocument> documents) {
        return documents.stream().map(CarteiraDocumentPresent::present).collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getQuantidadeAtivos() {
        return quantidadeAtivos;
    }
}
