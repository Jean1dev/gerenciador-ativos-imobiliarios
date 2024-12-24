package br.com.carteira.infra.marketplace.compra.ativos.component;

import br.com.carteira.dominio.ativo.Ativo;
import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;
import br.com.carteira.dominio.marketplace.compra.ativos.useCase.OrdemCompraGateway;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.marketplace.compra.ativos.mongodb.OrdemCompraAtivo;
import br.com.carteira.infra.marketplace.compra.ativos.mongodb.OrdemCompraAtivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultOrdemCompraGateway implements OrdemCompraGateway {
    @Autowired
    private OrdemCompraAtivoRepository repository;
    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;

    @Override
    public void registrarOrdem(OrdemCompra ordemCompra) {
        repository.save(OrdemCompraAtivo.fromDomain(ordemCompra));
    }

    @Override
    public boolean verificarSeUsuarioPossuiSaldo(String usuarioRef, double valor) {
        return true;
    }

    @Override
    public void retirarSaldoDoUsuario(String usuarioRef, Double valor) {

    }

    @Override
    public Optional<Ativo> buscarAtivo(String ativoRef) {
        return ativoComCotacaoRepository.findById(ativoRef)
                .flatMap(ativoComCotacao -> Optional.ofNullable(AtivoComCotacao.fromDocument(ativoComCotacao)));
    }
}
