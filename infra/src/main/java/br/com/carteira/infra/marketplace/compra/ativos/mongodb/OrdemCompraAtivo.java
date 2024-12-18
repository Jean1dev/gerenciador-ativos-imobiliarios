package br.com.carteira.infra.marketplace.compra.ativos.mongodb;

import br.com.carteira.dominio.marketplace.compra.ativos.OrdemCompra;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("ordem_compra_ativo")
public class OrdemCompraAtivo {
    @Id
    private String id;
    private String ativoComCotacaoRef;
    private String usuarioRef;
    private LocalDateTime quando;
    private Double quantidade;

    public OrdemCompraAtivo(
            String id,
            String ativoComCotacaoRef,
            String usuarioRef,
            LocalDateTime quando,
            Double quantidade
    ) {
        this.id = id;
        this.ativoComCotacaoRef = ativoComCotacaoRef;
        this.usuarioRef = usuarioRef;
        this.quando = quando;
        this.quantidade = quantidade;
    }

    public static OrdemCompraAtivo fromDomain(OrdemCompra domain) {
        return new OrdemCompraAtivo(
                domain.getId(),
                domain.getAtivoRef(),
                domain.getUsuarioRef(),
                domain.getQuando(),
                domain.getQuantidade()
        );
    }
}
