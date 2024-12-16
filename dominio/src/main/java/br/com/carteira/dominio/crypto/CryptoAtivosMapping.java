package br.com.carteira.dominio.crypto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CryptoAtivosMapping {
    public static final Map<String, List<String>> cryptoMap = Map.ofEntries(
            Map.entry("BITCOIN", List.of("BTC", "BITCOIN")),
            Map.entry("ETHEREUM", List.of("ETH", "ETHEREUM")),
            Map.entry("RIPPLE", List.of("XRP", "RIPPLE")),
            Map.entry("LITECOIN", List.of("LTC", "LITECOIN")),
            Map.entry("BITCOIN CASH", List.of("BCH", "BITCOIN CASH")),
            Map.entry("EOS", List.of("EOS")),
            Map.entry("STELLAR", List.of("XLM", "STELLAR")),
            Map.entry("CARDANO", List.of("ADA", "CARDANO")),
            Map.entry("TRON", List.of("TRX", "TRON")),
            Map.entry("IOTA", List.of("MIOTA", "IOTA")),
            Map.entry("DASH", List.of("DASH"))
    );

    public static List<String> listMapping() {
        return cryptoMap.values().stream().flatMap(List::stream).toList();
    }

    public static Optional<String> ifContainsGetName(String nome) {
        return cryptoMap.entrySet().stream()
                .filter(stringListEntry -> {
                    var key = stringListEntry.getKey();
                    var value = stringListEntry.getValue();
                    return key.equalsIgnoreCase(nome) || value.stream().anyMatch(s -> s.equalsIgnoreCase(nome));
                })
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
