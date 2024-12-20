package br.com.carteira.infra.usuario.service;

import br.com.carteira.infra.E2ETests;
import br.com.carteira.infra.usuario.UsuarioFactory;
import br.com.carteira.infra.usuario.mongodb.Usuario;
import br.com.carteira.infra.usuario.mongodb.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioServiceTest extends E2ETests {
    @Container
    public static MongoDBContainer MONGO_CONTAINER = new MongoDBContainer(DockerImageName.parse("mongo:6.0.5"));
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private UsuarioService service;

    @DynamicPropertySource
    public static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

    @BeforeAll
    public static void mongoIsUp() {
        assertTrue(MONGO_CONTAINER.isRunning());
    }

    @AfterEach
    public void cleanDB() {
        repository.deleteAll();
    }

    @Test
    void getUsuario() {
        var email = "email";
        var user = "user";
        Usuario finded = service.getUsuario(user, email);

        assertNotNull(finded.getId());
        assertEquals(user, finded.getName());
        assertEquals(email, finded.getEmail());
        assertEquals(0.0, finded.getSaldo());
    }

    @Test
    void needUsuario() {
        Usuario usuario = UsuarioFactory.umUsuarioSalvo(repository);
        Usuario finded = service.getUsuario(usuario.getName(), usuario.getEmail());

        assertEquals(usuario.getId(), finded.getId());
        assertEquals(usuario.getName(), finded.getName());
        assertEquals(usuario.getEmail(), finded.getEmail());
        assertEquals(usuario.getSaldo(), finded.getSaldo());
    }

    @Test
    void adicionarSaldoNoUsuario() {
        Usuario usuario = UsuarioFactory.umUsuarioSalvo(repository);
        service.adicionarSaldoNoUsuario(usuario.getName(), usuario.getEmail(), 100.0);
        Usuario finded = repository.findById(usuario.getId()).get();
        assertEquals(600.0, finded.getSaldo());
    }

    @Test
    void reduzirSaldoNoUsuario() {
        Usuario usuario = UsuarioFactory.umUsuarioSalvo(repository);
        service.reduzirSaldoNoUsuario(usuario.getName(), usuario.getEmail(), 100.0);
        Usuario finded = repository.findById(usuario.getId()).get();
        assertEquals(400.0, finded.getSaldo());
    }
}