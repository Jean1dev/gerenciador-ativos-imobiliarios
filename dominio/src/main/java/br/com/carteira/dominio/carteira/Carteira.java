package br.com.carteira.dominio.carteira;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.metas.Meta;

import java.util.Set;

public class Carteira {
    private String nome;
    private Meta meta;

    private Set<Ativo> ativos;

    public Carteira() {
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getNome() {
        return nome;
    }

    public Meta getMeta() {
        return meta;
    }

    public Set<Ativo> getAtivos() {
        return ativos;
    }

    public void setAtivos(Set<Ativo> ativos) {
        this.ativos = ativos;
    }
}
