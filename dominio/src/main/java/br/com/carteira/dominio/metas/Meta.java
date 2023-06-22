package br.com.carteira.dominio.metas;

import br.com.carteira.dominio.TipoAtivo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Meta {
    private List<AtivoComPercentual> ativoComPercentuals;

    public Meta() {
        resetarMetas();
    }

    private void resetarMetas() {
        var percentualMaximo = 100;
        var totalTipoAtivos = TipoAtivo.values().length;
        var percentualDeCada = (double) percentualMaximo / totalTipoAtivos;
        var decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        var percentaulArredondado = Double.parseDouble(decimalFormat.format(percentualDeCada));

        ativoComPercentuals = Arrays.stream(TipoAtivo.values())
                .map(tipoAtivo -> new AtivoComPercentual(percentaulArredondado, tipoAtivo))
                .collect(Collectors.toList());
    }

    public List<AtivoComPercentual> getAtivoComPercentuals() {
        return ativoComPercentuals;
    }
}
