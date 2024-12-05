package br.com.carteira.infra.integracoes;

public record CryptoPricesDto(
        String name,
        Double price
) {
}
