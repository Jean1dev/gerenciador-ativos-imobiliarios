package br.com.carteira.infra.marketplace.compra.ativos.service;

import br.com.carteira.infra.ativo.mongodb.AtivoComCotacao;
import br.com.carteira.infra.ativo.mongodb.AtivoComCotacaoRepository;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuarios;
import br.com.carteira.infra.ativo.mongodb.AtivoDosUsuariosRepository;
import br.com.carteira.infra.ativo.service.VariacaoAtivosService;
import br.com.carteira.infra.carteira.mongodb.CarteiraDocument;
import br.com.carteira.infra.carteira.mongodb.CarteiraRepository;
import br.com.carteira.infra.marketplace.compra.ativos.records.AtivoDisponivel;
import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListagemAtivosService {

    @Autowired
    private AtivoComCotacaoRepository ativoComCotacaoRepository;
    @Autowired
    private AtivoDosUsuariosRepository ativoDosUsuariosRepository;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CarteiraRepository carteiraRepository;
    @Autowired
    private VariacaoAtivosService variacaoAtivosService;

    public List<AtivoDisponivel> listarDisponiveis(String user, String email) {
        if (user == null || user.isEmpty() && email == null || email.isEmpty()) {
            return listarTodosAtivos();
        }

        var usuario = usuarioService.needUsuario(user, email);
        return listarAtivosDisponiveisParaUsuario(usuario);
    }

    private List<AtivoDisponivel> listarAtivosDisponiveisParaUsuario(Usuario usuario) {
        List<String> carteiraIds = carteiraRepository.findByUsuarioRef(usuario.getId())
                .stream().map(CarteiraDocument::getId)
                .toList();

        List<String> tickersUsuario = carteiraIds.stream()
                .map(carteiraId -> ativoDosUsuariosRepository.findAllByCarteiraRef(carteiraId))
                .flatMap(List::stream)
                .map(AtivoDosUsuarios::getTicker)
                .distinct()
                .toList();

        return listarTodosAtivos()
                .stream()
                .filter(ativoDisponivel -> !tickersUsuario.contains(ativoDisponivel.codigo()))
                .toList();
    }

    private List<AtivoDisponivel> listarTodosAtivos() {
        return ativoComCotacaoRepository.findAll()
                .stream()
                .map(this::buildAtivoDisponivelFull)
                .toList();
    }

    private AtivoDisponivel buildAtivoDisponivelFull(AtivoComCotacao ativoComCotacao) {
        var variacaoEntity = variacaoAtivosService.getVariacao(ativoComCotacao.getTicker());
        boolean variacaoPositiva = variacaoEntity.percentualVariacao() > 0;
        double variacao = variacaoEntity.percentualVariacao();

        return new AtivoDisponivel(
                ativoComCotacao.getImage(),
                ativoComCotacao.getTicker(),
                ativoComCotacao.getTicker(),
                true,
                ativoComCotacao.getValor(),
                variacao,
                variacaoPositiva
        );
    }

}
