package br.com.carteira.infra.admin.service;

import br.com.carteira.infra.admin.records.AtivosComProblema;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AtivosComProblemasService {

    public static final String ATIVOS_COM_PROBLEMAS_EVIDENCIAS = "ativos_com_problemas_evidencias";
    private final MongoOperations mongoOperations;
    private final AtivoComCotacaoRepository ativoComCotacaoRepository;
    private final AtivoDosUsuariosRepository ativoDosUsuariosRepository;

    public AtivosComProblemasService(
            MongoOperations mongoOperations,
            AtivoComCotacaoRepository ativoComCotacaoRepository,
            AtivoDosUsuariosRepository ativoDosUsuariosRepository) {
        this.mongoOperations = mongoOperations;
        this.ativoComCotacaoRepository = ativoComCotacaoRepository;
        this.ativoDosUsuariosRepository = ativoDosUsuariosRepository;
    }

    public void evidenciar(String ticker, String message) {
        var ativoComProblema = new AtivosComProblema(ticker, LocalDateTime.now(), message);
        mongoOperations.save(ativoComProblema, ATIVOS_COM_PROBLEMAS_EVIDENCIAS);
    }

    public void corrigirFalhas() {
        mongoOperations.findAll(AtivosComProblema.class, ATIVOS_COM_PROBLEMAS_EVIDENCIAS)
                .forEach(ativosComProblema -> {
                    ativoComCotacaoRepository
                            .findByTicker(ativosComProblema.ticker())
                            .ifPresent(ativoComCotacaoRepository::delete);

                    ativoDosUsuariosRepository.deleteAllByTicker(ativosComProblema.ticker());
                });

        mongoOperations.dropCollection(ATIVOS_COM_PROBLEMAS_EVIDENCIAS);
    }
}
