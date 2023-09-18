package br.com.carteira.infra.ativo;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;

public class AtivoDosUsuariosFactory {

    public static AtivoDosUsuarios simples(String carteiraRef, TipoAtivo tipoAtivo) {
        return new AtivoDosUsuarios(null, carteiraRef, tipoAtivo, "", 0.0, 0.0, 0, 0.0, 0, "", "", null);
    }
}
