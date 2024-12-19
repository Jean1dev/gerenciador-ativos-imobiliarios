package br.com.carteira.infra.marketplace.compra.ativos.records;

public record AtivoDisponivel(
        String imgUrl,
        String nome,
        String codigo,
        boolean disponivel,
        double valor,
        double variacao,
        boolean variacaoPositiva
) {
}
