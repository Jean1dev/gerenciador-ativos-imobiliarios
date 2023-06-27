package br.com.carteira.infra.ativo.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ativo")
public class AtivosDocument {
    @Id
    private String id;
    private String ticker;
    private double quantidade;
    private double nota;

    public AtivosDocument(double quantidade, double nota) {
        this.quantidade = quantidade;
        this.nota = nota;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public double getNota() {
        return nota;
    }
}
