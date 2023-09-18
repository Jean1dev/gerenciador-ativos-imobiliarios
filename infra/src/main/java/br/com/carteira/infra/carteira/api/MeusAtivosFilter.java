package br.com.carteira.infra.carteira.api;

import java.util.List;

public class MeusAtivosFilter {
    private List<String> tipos;
    private List<String> carteiras;

    public MeusAtivosFilter(List<String> tipos, List<String> carteiras) {
        this.tipos = tipos;
        this.carteiras = carteiras;
    }

    public List<String> getTipos() {
        return tipos;
    }

    public List<String> getCarteiras() {
        return carteiras;
    }
}
