package br.com.carteira.infra.admin.service;

import br.com.carteira.infra.admin.records.AtivosComProblema;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
public class AtivosComProblemasService {

    public static final String COLLECTION_NAME = "ativos_com_problemas_evidencias";
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

    public void evidenciar(String ticker) {
        mongoOperations.save(new AtivosComProblema(ticker), COLLECTION_NAME);
    }

    public void corrigirFalhas() {
        mongoOperations.findAll(AtivosComProblema.class, COLLECTION_NAME)
                .forEach(ativosComProblema -> {
                    ativoComCotacaoRepository
                            .findByTicker(ativosComProblema.ticker())
                            .ifPresent(ativoComCotacaoRepository::delete);

                    ativoDosUsuariosRepository.deleteAllByTicker(ativosComProblema.ticker());
                });

        mongoOperations.dropCollection(COLLECTION_NAME);
    }
}
