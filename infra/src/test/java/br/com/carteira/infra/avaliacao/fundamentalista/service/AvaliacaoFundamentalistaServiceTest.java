package br.com.carteira.infra.avaliacao.fundamentalista.service;

import br.com.carteira.dominio.ativo.TipoAtivo;
import br.com.carteira.infra.avaliacao.fundamentalista.api.AvaliacaoAtivoDTO;
import br.com.carteira.infra.avaliacao.fundamentalista.api.RelatorioFundamentalistaDTO;
import br.com.carteira.infra.avaliacao.fundamentalista.api.RespostaCriterioDTO;
import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.AvaliacaoFundamentalistaDocument;
import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.AvaliacaoFundamentalistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvaliacaoFundamentalistaService")
class AvaliacaoFundamentalistaServiceTest {

    @Mock
    private AvaliacaoFundamentalistaRepository repository;

    @Captor
    private ArgumentCaptor<List<AvaliacaoFundamentalistaDocument>> documentsCaptor;

    @InjectMocks
    private AvaliacaoFundamentalistaService service;

    private RelatorioFundamentalistaDTO relatorioDTO;

    @BeforeEach
    void setUp() {
        relatorioDTO = new RelatorioFundamentalistaDTO(
                "2026-03-17T20:18:01.911Z",
                List.of(
                        new AvaliacaoAtivoDTO(
                                "SAPR4",
                                "SAPR4",
                                TipoAtivo.ACAO_NACIONAL,
                                7.0,
                                List.of(
                                        new RespostaCriterioDTO("ROE historicamente maior que 5%?", true, "ROE atual de 16,8%.")
                                ),
                                "2026-03-17T20:17:50.292Z",
                                List.of("https://www.fundamentus.com.br/detalhes.php?papel=sapr4")
                        ),
                        new AvaliacaoAtivoDTO(
                                "XPML11",
                                "XPML11",
                                TipoAtivo.FII,
                                6.0,
                                List.of(
                                        new RespostaCriterioDTO("P/VP abaixo de 1?", true, "P/VP 0,95.")
                                ),
                                "2026-03-17T20:18:00.000Z",
                                List.of("https://www.fundsexplorer.com.br/funds/xpml11")
                        )
                )
        );
    }

    @Test
    @DisplayName("listarTodas retorna lista do repository ordenada por nota")
    void listarTodas() {
        var doc1 = documento("SAPR4", 7.0);
        var doc2 = documento("XPML11", 6.0);
        when(repository.findAllByOrderByNotaDesc()).thenReturn(List.of(doc1, doc2));

        List<AvaliacaoFundamentalistaDocument> resultado = service.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("SAPR4", resultado.get(0).getCodigo());
        assertEquals(7.0, resultado.get(0).getNota());
        assertEquals("XPML11", resultado.get(1).getCodigo());
        assertEquals(6.0, resultado.get(1).getNota());
        verify(repository).findAllByOrderByNotaDesc();
    }

    @Test
    @DisplayName("salvarRelatorio converte DTO e chama saveAll com documentos corretos")
    void salvarRelatorio() {
        service.salvarRelatorio(relatorioDTO);

        verify(repository).saveAll(documentsCaptor.capture());
        List<AvaliacaoFundamentalistaDocument> saved = documentsCaptor.getValue();
        assertEquals(2, saved.size());

        AvaliacaoFundamentalistaDocument first = saved.get(0);
        assertEquals("SAPR4", first.getId());
        assertEquals("SAPR4", first.getCodigo());
        assertEquals("SAPR4", first.getNome());
        assertEquals(TipoAtivo.ACAO_NACIONAL, first.getTipoAtivo());
        assertEquals(7.0, first.getNota());
        assertNotNull(first.getDataAvaliacao());
        assertNotNull(first.getGeradoEm());
        assertEquals(1, first.getRespostas().size());
        assertEquals("ROE historicamente maior que 5%?", first.getRespostas().get(0).getPergunta());
        assertEquals(true, first.getRespostas().get(0).isResposta());
        assertEquals("ROE atual de 16,8%.", first.getRespostas().get(0).getJustificativa());
        assertEquals(1, first.getFontesUtilizadas().size());
        assertEquals("https://www.fundamentus.com.br/detalhes.php?papel=sapr4", first.getFontesUtilizadas().get(0));

        AvaliacaoFundamentalistaDocument second = saved.get(1);
        assertEquals("XPML11", second.getId());
        assertEquals(TipoAtivo.FII, second.getTipoAtivo());
        assertEquals(6.0, second.getNota());
    }

    private static AvaliacaoFundamentalistaDocument documento(String codigo, double nota) {
        var doc = new AvaliacaoFundamentalistaDocument();
        doc.setId(codigo);
        doc.setCodigo(codigo);
        doc.setNome(codigo);
        doc.setNota(nota);
        return doc;
    }
}
