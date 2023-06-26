package br.com.carteira.infra.carteira.mongodb;

import br.com.carteira.dominio.metas.Meta;
import br.com.carteira.infra.ativo.mongodb.AtivosDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("carteira")
public class CarteiraDocument {
    @Id
    private String id;
    private String nome;
    private Meta meta;
    private Set<AtivosDocument> ativosRef;
    private String usuarioRef;

    public CarteiraDocument(String id, String nome, Meta meta, Set<AtivosDocument> ativosRef, String usuarioRef) {
        this.id = id;
        this.nome = nome;
        this.meta = meta;
        this.ativosRef = ativosRef;
        this.usuarioRef = usuarioRef;
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

    public Set<AtivosDocument> getAtivosRef() {
        return ativosRef;
    }

    public String getUsuarioRef() {
        return usuarioRef;
    }
}
