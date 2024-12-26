package br.com.carteira.infra.ativo.api;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.dominio.ativo.useCase.records.AdicionarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AportarAtivoInput;
import br.com.carteira.dominio.ativo.useCase.records.AtualizarAtivoInput;
import br.com.carteira.dominio.criterios.Criterio;
import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Ativo Controller")
class AtivoControllerTest extends E2ETests {

    @Autowired
    public CarteiraRepository carteiraRepository;
    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;
    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;

    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @Test
    @DisplayName("deve encontrar um ativo pelo ticker")
    void findIdByTIcker() throws Exception {
        AtivoComCotacao anim3 = ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("ANIM3", TipoAtivo.ACAO_NACIONAL));
        final var request = get("/ativo/id")
                .queryParam("ticker", "ANIM3")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(anim3.getId()));
    }

    @Test
    @DisplayName("Deve trazer uma lista de sugestao de ativos")
    public void sugestao() throws Exception {
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("PETR4", TipoAtivo.ACAO_NACIONAL));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("PETR3", TipoAtivo.ACAO_NACIONAL));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("KLBN11", TipoAtivo.ACAO_NACIONAL));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("XPML11", TipoAtivo.FII));
        ativoComCotacaoRepository.save(AtivoComCotacao.criarCotacao("AAPL", TipoAtivo.ACAO_INTERNACIONAL));
        final var request = get("/ativo/sugestao")
                .queryParam("query", "PE")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        var mapper = new ObjectMapper();
        MvcResult mvcResult = response.andExpect(status().isOk()).andReturn();
        List<String> readValue = mapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        assertEquals(2, readValue.size());
        assertTrue(readValue.contains("PETR3"));
        assertTrue(readValue.contains("PETR4"));

        final var request2 = get("/ativo/sugestao")
                .queryParam("query", "a")
                .contentType(MediaType.APPLICATION_JSON);

        final var response2 = this.mvc.perform(request2)
                .andDo(print());

        mvcResult = response2.andExpect(status().isOk()).andReturn();
        readValue = mapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        assertEquals(1, readValue.size());
        assertTrue(readValue.contains("AAPL"));
    }

    @Test
    @DisplayName("deve aportar em um ativo")
    public void deveAportar() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste22", null, null, 1)).getId();
        var idAtivo = ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                idCarteira,
                null,
                null,
                0,
                0,
                0,
                0.0,
                0.0,
                "String ticker",
                null,
                null)).getId();

        final var input = new AportarAtivoInput(idAtivo, 5.0);
        final var request = post("/ativo/aportar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var ativoDosUsuarios = ativoDosUsuariosRepository.findById(idAtivo).orElseThrow();
        assertEquals(5.0, ativoDosUsuarios.getQuantidade());
    }

    @Test
    @DisplayName("deve atualizar o valor de um ativo")
    public void deveAtualizarValor() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste222", null, null, 1)).getId();
        var idAtivo = ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                idCarteira,
                TipoAtivo.RENDA_FIXA,
                null,
                0,
                0,
                0,
                0.0,
                0.0,
                "Tesouro nacional",
                null,
                null)).getId();

        final var input = new AtualizarAtivoInput(
                1.0,
                5,
                idAtivo,
                List.of(new Criterio("FAKE", "FAKE")),
                150.0
        );

        final var request = put("/ativo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var ativoDosUsuarios = ativoDosUsuariosRepository.findById(idAtivo).orElseThrow();
        var criterio = ativoDosUsuarios.getCriterios().get(0);
        assertEquals("FAKE", criterio.getCriterio());
        assertEquals("FAKE", criterio.getPergunta());
        assertEquals(false, criterio.getSimOuNao());
        assertEquals(1.0, ativoDosUsuarios.getQuantidade());
        assertEquals(5, ativoDosUsuarios.getNota());
        assertEquals(150.0, ativoDosUsuarios.getValorAtual());
    }

    @Test
    @DisplayName("Deve atualizar um ativo")
    public void deveAtualizarAtivo() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste22", null, null, 1)).getId();
        var idAtivo = ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                idCarteira,
                null,
                null,
                0,
                0,
                0,
                0.0,
                0.0,
                "String ticker",
                null,
                null)).getId();

        final var input = new AtualizarAtivoInput(
                1.0,
                5,
                idAtivo,
                List.of(new Criterio("FAKE", "FAKE"))
        );

        final var request = put("/ativo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var ativoDosUsuarios = ativoDosUsuariosRepository.findById(idAtivo).orElseThrow();
        var criterio = ativoDosUsuarios.getCriterios().get(0);
        assertEquals("FAKE", criterio.getCriterio());
        assertEquals("FAKE", criterio.getPergunta());
        assertEquals(false, criterio.getSimOuNao());
        assertEquals(1.0, ativoDosUsuarios.getQuantidade());
        assertEquals(5, ativoDosUsuarios.getNota());
        assertEquals(0, ativoDosUsuarios.getValorAtual());
    }

    @Test
    @DisplayName("deve adicionar uma acao")
    public void deveAdicionarUmaAcao() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste", null, null, 0)).getId();
        var ativoInput = new AdicionarAtivoInput(
                TipoAtivo.ACAO_INTERNACIONAL,
                5,
                0.0,
                "NASDAQ",
                "String tipoAlocacao",
                1.0,
                "AAPL",
                idCarteira,
                null
        );

        final var request = post("/ativo")
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email")
                .content(mapper.writeValueAsString(ativoInput));

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(1, carteiraRepository.findById(idCarteira).get().getQuantidadeAtivos());
    }

    @Test
    @DisplayName("Deve adicionar uma ativo Renda fixa e salvar o valor dele")
    public void rendaFixa() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste", null, null, 0)).getId();
        var ativoInput = new AdicionarAtivoInput(
                TipoAtivo.RENDA_FIXA,
                5,
                10000.0,
                "TESOURO NACIONAL",
                "TESOURO PRE-FIXADO",
                1.0,
                "TESOURO PRE-FIXADO",
                idCarteira,
                null
        );

        this.mvc.perform(post("/ativo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("user", "user")
                        .header("email", "email")
                        .content(mapper.writeValueAsString(ativoInput)))
                .andDo(print())
                .andExpect(status().isOk());

        double valorAtual = ativoDosUsuariosRepository.findAll()
                .stream().filter(ativoDosUsuarios -> ativoDosUsuarios.getTicker().equals("TESOURO PRE-FIXADO"))
                .findFirst()
                .orElseThrow()
                .getValorAtual();

        assertEquals(10000.0, valorAtual);
    }

    @Test
    @DisplayName("deve remover um ativo da carteira")
    public void deveRemover() throws Exception {
        var idCarteira = carteiraRepository.save(new CarteiraDocument(null, "teste2", null, null, 1)).getId();
        var stringTicker = ativoDosUsuariosRepository.save(new AtivoDosUsuarios(
                null,
                idCarteira,
                null,
                null,
                0,
                0,
                0,
                0.0,
                0.0,
                "String ticker",
                null,
                null)).getId();

        final var request = delete("/ativo/" + stringTicker)
                .contentType(MediaType.APPLICATION_JSON)
                .header("user", "user")
                .header("email", "email");

        this.mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());

        var byId = ativoDosUsuariosRepository.findById(stringTicker);
        assertTrue(byId.isEmpty());
        assertEquals(0, carteiraRepository.findById(idCarteira).get().getQuantidadeAtivos());
    }

    @Test
    @DisplayName("Deve retornar todos os enums de ativos")
    public void deveRetornar() throws Exception {
        final var request = get("/ativo/tipo-ativos")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(request)
                .andDo(print());

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.TipoAtivo[0].ACAO_NACIONAL").value("Ações Nacionais"))
                .andExpect(jsonPath("$.TipoAtivo[1].ACAO_INTERNACIONAL").value("Ações Internacionais"))
                .andExpect(jsonPath("$.TipoAtivo[2].REITs").value("Real Estate"))
                .andExpect(jsonPath("$.TipoAtivo[3].FII").value("Fundos Imobiliarios"))
                .andExpect(jsonPath("$.TipoAtivo[4].CRYPTO").value("Cryptomoedas"))
                .andExpect(jsonPath("$.TipoAtivo[5].RENDA_FIXA").value("Renda Fixa"));
    }

}