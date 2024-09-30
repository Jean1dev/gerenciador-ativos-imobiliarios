package br.com.carteira.infra.integracoes;

public record CotacaoDto(
        Double valor,
        boolean hasError,
        String errorMessage
) {
    public static CotacaoDto fromPrice(Double price) {
        return new CotacaoDto(price, false, null);
    }
}
