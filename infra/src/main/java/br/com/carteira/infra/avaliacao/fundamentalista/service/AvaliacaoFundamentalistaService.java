package br.com.carteira.infra.avaliacao.fundamentalista.service;

import br.com.carteira.infra.avaliacao.fundamentalista.api.AvaliacaoAtivoDTO;
import br.com.carteira.infra.avaliacao.fundamentalista.api.RelatorioFundamentalistaDTO;
import br.com.carteira.infra.avaliacao.fundamentalista.api.RespostaCriterioDTO;
import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.AvaliacaoFundamentalistaDocument;
import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.AvaliacaoFundamentalistaRepository;
import br.com.carteira.infra.avaliacao.fundamentalista.mongodb.RespostaCriterioDocument;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvaliacaoFundamentalistaService {

    private final AvaliacaoFundamentalistaRepository repository;

    public AvaliacaoFundamentalistaService(AvaliacaoFundamentalistaRepository repository) {
        this.repository = repository;
    }

    public List<AvaliacaoFundamentalistaDocument> listarTodas() {
        return repository.findAllByOrderByNotaDesc();
    }

    public void salvarRelatorio(RelatorioFundamentalistaDTO dto) {
        Instant geradoEm = Instant.parse(dto.geradoEm());
        List<AvaliacaoFundamentalistaDocument> documents = dto.ativos().stream()
                .map(a -> toDocument(a, geradoEm))
                .collect(Collectors.toList());
        repository.saveAll(documents);
    }

    private static AvaliacaoFundamentalistaDocument toDocument(AvaliacaoAtivoDTO dto, Instant geradoEm) {
        List<RespostaCriterioDocument> respostas = dto.respostas().stream()
                .map(AvaliacaoFundamentalistaService::toRespostaDocument)
                .collect(Collectors.toList());
        Instant dataAvaliacao = Instant.parse(dto.dataAvaliacao());
        return new AvaliacaoFundamentalistaDocument(
                dto.codigo(),
                dto.codigo(),
                dto.nome(),
                dto.tipoAtivo(),
                dto.nota(),
                dataAvaliacao,
                dto.fontesUtilizadas(),
                respostas,
                geradoEm
        );
    }

    private static RespostaCriterioDocument toRespostaDocument(RespostaCriterioDTO dto) {
        return new RespostaCriterioDocument(dto.pergunta(), dto.resposta(), dto.justificativa());
    }
}
