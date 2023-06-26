package br.com.carteira.infra.carteira.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.carteira.useCase.records.AtivoSimplificado;
import br.com.carteira.dominio.carteira.useCase.records.CriarCarteiraInput;
import br.com.carteira.dominio.metas.MetaDefinida;
import br.com.carteira.infra.E2ETests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Cateira test")
class CarteiraControllerTest extends E2ETests {

    @Test
    @DisplayName("deve criar uma carteira")
    public void deveCriar() throws Exception {
        var input = CriarCarteiraInput.byName("nome");
        final var request = post("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve criar uma carteira com payload completo")
    public void deveCriarCompleto() throws Exception {
        Collection<AtivoSimplificado> ativos = List.of(
                new AtivoSimplificado(TipoAtivo.ACAO_NACIONAL, "PETR4", 5, 4),
                new AtivoSimplificado(TipoAtivo.ACAO_INTERNACIONAL, "aapl4", 5, 4)
        );
        var input = new CriarCarteiraInput("Teste", null, ativos, MetaDefinida.META_DO_JEAN);
        final var request = post("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(input));

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk());
    }

    @Test
    @DisplayName("deve buscar minhas carteiras")
    public void deveBuscarMinhasCarteiras() throws Exception {
        var input = CriarCarteiraInput.byName("nome");

        final var request = post("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(input));

        this.mvc.perform(request).andDo(print());

        final var minhasCarteirasRequest = get("/carteira")
                .contentType(MediaType.APPLICATION_JSON)
                .param("user", "user")
                .param("email", "email");

        final var response = this.mvc.perform(minhasCarteirasRequest)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", instanceOf(String.class)))
                .andExpect(jsonPath("$[0].nome").value("nome"))
                .andExpect(jsonPath("$[0].quantidadeAtivos").value(0));
    }

}