package br.com.carteira.infra.carteira.mongodb;

import br.com.carteira.dominio.carteira.Carteira;
import br.com.carteira.dominio.metas.Meta;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;

@Document("carteira")
public class CarteiraDocument {
    @Id
    private String id;
    private String nome;
    private Meta meta;
    private String usuarioRef;
    private int quantidadeAtivos;

    public CarteiraDocument(String id, String nome, Meta meta, String usuarioRef, int quantidadeAtivos) {
        this.id = id;
        this.nome = nome;
        this.meta = meta;
        this.usuarioRef = usuarioRef;
        this.quantidadeAtivos = quantidadeAtivos;
    }

    public static Carteira simplificadoFromDocument(CarteiraDocument carteiraDocument) {
        var carteira = new Carteira();
        carteira.setIdentificacao(carteiraDocument.getId());
        carteira.setNome(carteiraDocument.getNome());
        carteira.setMeta(carteiraDocument.getMeta());
        carteira.setAtivos(Collections.emptySet());
        carteira.setQuantidadeAtivos(carteiraDocument.getQuantidadeAtivos());
        carteira.setPertenceAoUsuarioRef(carteiraDocument.getUsuarioRef());
        return carteira;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Meta getMeta() {
        return meta;
    }

    public String getUsuarioRef() {
        return usuarioRef;
    }

    public int getQuantidadeAtivos() {
        return quantidadeAtivos;
    }
}
