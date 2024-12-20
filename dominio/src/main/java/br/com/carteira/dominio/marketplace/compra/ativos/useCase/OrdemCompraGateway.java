package br.com.carteira.dominio.marketplace.compra.ativos.useCase;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;

import java.util.Optional;

public interface OrdemCompraGateway {

    void registrarOrdem(OrdemCompra ordemCompra);

    boolean verificarSeUsuarioPossuiSaldo(String usuarioRef, double valor);

    void retirarSaldoDoUsuario(String usuarioRef, Double valor);

    Optional<Ativo> buscarAtivo(String ativoRef);
}
