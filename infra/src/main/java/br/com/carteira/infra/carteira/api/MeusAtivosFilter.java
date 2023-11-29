package br.com.carteira.infra.carteira.api;

import java.util.List;

public class MeusAtivosFilter {
    private List<String> tipos;
    private List<String> carteiras;
    private String terms;

    public MeusAtivosFilter(List<String> tipos, List<String> carteiras, String terms) {
        this.tipos = tipos;
        this.carteiras = carteiras;
        this.terms = terms;
    }

    public List<String> getTipos() {
        return tipos;
    }

    public List<String> getCarteiras() {
        return carteiras;
    }

    public String getTerms() {
        return terms;
    }
}
