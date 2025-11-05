package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.repository.ColeccionRepository;
import es.unex.cum.mdai.movieTracker.data.repository.PeliculaRepository;
import es.unex.cum.mdai.movieTracker.data.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ColeccionRepositoryTest {

    @Autowired
    private ColeccionRepository coleccionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Pelicula pelicula1;
    private Pelicula pelicula2;
    private Coleccion coleccion1;
    private Coleccion coleccion2;

    @BeforeEach
    void setUp() {
        // Recuperar usuarios y películas insertados por data.sql
        usuario1 = usuarioRepository.findByUsername("alicia");
        usuario2 = usuarioRepository.findByUsername("juanp");

        pelicula1 = peliculaRepository.findByTitulo("El Padrino");
        pelicula2 = peliculaRepository.findByTitulo("Inception");

        // Asegurar que existen
        assertNotNull(usuario1, "usuario1 no encontrado en la BD de test");
        assertNotNull(usuario2, "usuario2 no encontrado en la BD de test");
        assertNotNull(pelicula1, "pelicula1 no encontrada en la BD de test");
        assertNotNull(pelicula2, "pelicula2 no encontrada en la BD de test");

        // Recuperar colecciones creadas por data.sql
        Optional<Coleccion> opt1 = coleccionRepository.findByUsuario_IdUsuario(usuario1.getIdUsuario());
        Optional<Coleccion> opt2 = coleccionRepository.findByUsuario_IdUsuario(usuario2.getIdUsuario());

        assertTrue(opt1.isPresent(), "Coleccion de usuario1 no encontrada en data.sql");
        assertTrue(opt2.isPresent(), "Coleccion de usuario2 no encontrada en data.sql");

        coleccion1 = opt1.get();
        coleccion2 = opt2.get();

        // En ocasiones JPA no rellena la lista inmediatamente si la relación es lazy; forzamos acceso
        coleccion1.getListaPeliculas().size();
        coleccion2.getListaPeliculas().size();

        // Asociar a los usuarios para tests que usan usuario.getColeccion()
        usuario1.setColeccion(coleccion1);
        usuario2.setColeccion(coleccion2);
    }

    @Test
    void testFindByUsuario_IdUsuario() {
        Optional<Coleccion> opt = coleccionRepository.findByUsuario_IdUsuario(usuario1.getIdUsuario());
        assertTrue(opt.isPresent());
        Coleccion encontrada = opt.get();
        assertEquals(usuario1.getIdUsuario(), encontrada.getUsuario().getIdUsuario());

        Optional<Coleccion> optNo = coleccionRepository.findByUsuario_IdUsuario(999L);
        assertFalse(optNo.isPresent());
    }

    @Test
    void testExistsByUsuario_IdUsuario() {
        assertTrue(coleccionRepository.existsByUsuario_IdUsuario(usuario1.getIdUsuario()));
        assertFalse(coleccionRepository.existsByUsuario_IdUsuario(999L));
    }

    @Test
    void testFindByListaPeliculas_IdPelicula() {
        List<Coleccion> colsPeli1 = coleccionRepository.findByListaPeliculas_IdPelicula(pelicula1.getIdPelicula());
        assertNotNull(colsPeli1);
        assertEquals(2, colsPeli1.size());

        // Comprobar que las colecciones devueltas pertenecen a los usuarios esperados
        List<Long> usuariosIds = List.of(colsPeli1.get(0).getUsuario().getIdUsuario(), colsPeli1.get(1).getUsuario().getIdUsuario());
        assertTrue(usuariosIds.contains(usuario1.getIdUsuario()));
        assertTrue(usuariosIds.contains(usuario2.getIdUsuario()));

        List<Coleccion> colsPeli2 = coleccionRepository.findByListaPeliculas_IdPelicula(pelicula2.getIdPelicula());
        assertNotNull(colsPeli2);
        assertEquals(1, colsPeli2.size());
        assertEquals(usuario1.getIdUsuario(), colsPeli2.get(0).getUsuario().getIdUsuario());

        // Película inexistente
        List<Coleccion> colsNo = coleccionRepository.findByListaPeliculas_IdPelicula(999L);
        assertNotNull(colsNo);
        assertTrue(colsNo.isEmpty());
    }

    @Test
    void testExistsByUsuario_IdUsuarioAndListaPeliculas_IdPelicula() {
        assertTrue(coleccionRepository.existsByUsuario_IdUsuarioAndListaPeliculas_IdPelicula(
                usuario1.getIdUsuario(), pelicula2.getIdPelicula()));

        assertFalse(coleccionRepository.existsByUsuario_IdUsuarioAndListaPeliculas_IdPelicula(
                usuario2.getIdUsuario(), pelicula2.getIdPelicula()));
    }

    @Test
    void testFindByIdColeccionAndUsuario_IdUsuario() {
        Optional<Coleccion> opt = coleccionRepository.findByIdColeccionAndUsuario_IdUsuario(coleccion1.getIdColeccion(), usuario1.getIdUsuario());
        assertTrue(opt.isPresent());
        Coleccion encontrada = opt.get();
        assertEquals(coleccion1.getIdColeccion(), encontrada.getIdColeccion());

        Optional<Coleccion> optNo = coleccionRepository.findByIdColeccionAndUsuario_IdUsuario(coleccion1.getIdColeccion(), 999L);
        assertFalse(optNo.isPresent());
    }

    @Test
    void testCrudColeccion() {
        // Guardar nueva coleccion para un nuevo usuario (usar datos coherentes)
        Usuario nuevo = new Usuario("Emilio", "Delgado", "Muñoz", "emilio.delgado@test.com", "emi", "emi123");
        usuarioRepository.save(nuevo);

        Optional<Coleccion> opt1 = coleccionRepository.findByUsuario_IdUsuario(nuevo.getIdUsuario());
        assertTrue(opt1.isPresent(), "Coleccion de emi no encontrada en data.sql");

        Coleccion guardada = opt1.get();
        assertNotNull(guardada.getIdColeccion());

        // Añadir pelicula y actualizar
        guardada.getListaPeliculas().add(pelicula2);
        coleccionRepository.save(guardada);

        Optional<Coleccion> recuperadaOpt = coleccionRepository.findByUsuario_IdUsuario(nuevo.getIdUsuario());
        assertTrue(recuperadaOpt.isPresent());
        Coleccion recuperada = recuperadaOpt.get();
        assertEquals(1, recuperada.getListaPeliculas().size());

        // Eliminar colección
        Long idColec = recuperada.getIdColeccion();
        coleccionRepository.delete(recuperada);

        assertFalse(coleccionRepository.findById(idColec).isPresent());

        // Comprobar que la película y el usuario siguen existiendo (no cascade desde colección a película/usuario)
        assertNotNull(usuarioRepository.findByIdUsuario(nuevo.getIdUsuario()));
        Pelicula peli = peliculaRepository.findByIdPelicula(pelicula2.getIdPelicula());
        assertNotNull(peli);
    }
}
