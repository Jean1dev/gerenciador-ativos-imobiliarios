package br.com.carteira.infra.criterio.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.E2ETests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Criterio APi Test")
class CriterioControllerTest extends E2ETests {

    @Test
    @DisplayName("deve trazer os criterios corretos")
    public void deveTrazer() throws Exception {
        final var requestBuilder = get("/criterio")
                .contentType(MediaType.APPLICATION_JSON)
                .param("tipo", TipoAtivo.ACAO_NACIONAL.name());

        final var response = this.mvc.perform(requestBuilder)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].criterio").value("ROE"))
                .andExpect(jsonPath("$[0].pergunta").value("ROE historicamente maior que 5%? (Considere anos anteriores)."));

        final var servletRequestBuilder = get("/criterio")
                .contentType(MediaType.APPLICATION_JSON)
                .param("tipo", TipoAtivo.FII.name());

        final var resultActions = this.mvc.perform(servletRequestBuilder)
                .andDo(print());

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].criterio").value(""))
                .andExpect(jsonPath("$[0].pergunta").value("As propriedades são novas e não consomem manutenção excessiva?"));
    }
}