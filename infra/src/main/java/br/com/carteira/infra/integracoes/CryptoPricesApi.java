package br.com.carteira.infra.integracoes;

import java.util.List;

public interface CryptoPricesApi {

    List<CryptoPricesDto> get();
}
