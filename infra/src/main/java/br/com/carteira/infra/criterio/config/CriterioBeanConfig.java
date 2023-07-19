package br.com.carteira.infra.criterio.config;

import br.com.carteira.dominio.criterios.useCase.TrazerDiagramaCorretoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CriterioBeanConfig {

    @Bean
    public TrazerDiagramaCorretoUseCase trazerDiagramaCorretoUseCase() {
        return new TrazerDiagramaCorretoUseCase();
    }
}
