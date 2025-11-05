package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.repository.PeliculaRepository;
import es.unex.cum.mdai.movieTracker.data.repository.UsuarioRepository;
import es.unex.cum.mdai.movieTracker.data.repository.ValoracionRepository;
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
public class ValoracionRepositoryTest {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Pelicula pelicula1;
    private Pelicula pelicula2;

    @BeforeEach
    void setUp() {
        // Recuperar las entidades insertadas por data.sql
        usuario1 = usuarioRepository.findByUsername("alicia");
        usuario2 = usuarioRepository.findByUsername("juanp");

        pelicula1 = peliculaRepository.findByTitulo("El Padrino");
        pelicula2 = peliculaRepository.findByTitulo("Inception");

        // Asegurar que los objetos existen en la BD de test
        assertNotNull(usuario1, "usuario1 no encontrado en la BD de test");
        assertNotNull(usuario2, "usuario2 no encontrado en la BD de test");
        assertNotNull(pelicula1, "pelicula1 no encontrada en la BD de test");
        assertNotNull(pelicula2, "pelicula2 no encontrada en la BD de test");
    }

    @Test
    void testFindByUsuario_IdUsuario() {
        // Crear valoraciones
        Valoracion v1 = new Valoracion(usuario1, pelicula1, 7, "Buena");
        Valoracion v2 = new Valoracion(usuario1, pelicula2, 9, "Excelente");
        Valoracion v3 = new Valoracion(usuario2, pelicula1, 5, "Regular");
        valoracionRepository.save(v1);
        valoracionRepository.save(v2);
        valoracionRepository.save(v3);

        List<Valoracion> valsUsuario1 = valoracionRepository.findByUsuario_IdUsuario(usuario1.getIdUsuario());
        assertNotNull(valsUsuario1);
        assertEquals(2, valsUsuario1.size());

        // Comprobar que las puntuaciones coinciden con las creadas
        assertEquals(7, valsUsuario1.get(0).getPuntuacion());
        assertEquals(9, valsUsuario1.get(1).getPuntuacion());

        // Usuario inexistente -> lista vacía
        List<Valoracion> valsNoUser = valoracionRepository.findByUsuario_IdUsuario(999L);
        assertNotNull(valsNoUser);
        assertTrue(valsNoUser.isEmpty());
    }

    @Test
    void testFindByPelicula_IdPelicula() {
        // Crear valoraciones
        Valoracion v1 = new Valoracion(usuario1, pelicula1, 8, "Muy buena");
        Valoracion v2 = new Valoracion(usuario2, pelicula1, 6, "Aceptable");
        Valoracion v3 = new Valoracion(usuario1, pelicula2, 4, "Mala");
        valoracionRepository.save(v1);
        valoracionRepository.save(v2);
        valoracionRepository.save(v3);

        List<Valoracion> valsPeli1 = valoracionRepository.findByPelicula_IdPelicula(pelicula1.getIdPelicula());
        assertNotNull(valsPeli1);
        assertEquals(2, valsPeli1.size());

        assertEquals(8, valsPeli1.get(0).getPuntuacion());
        assertEquals(6, valsPeli1.get(1).getPuntuacion());

        // Película inexistente -> lista vacía
        List<Valoracion> valsNoPeli = valoracionRepository.findByPelicula_IdPelicula(999L);
        assertNotNull(valsNoPeli);
        assertTrue(valsNoPeli.isEmpty());
    }

    @Test
    void testFindByUsuarioAndPelicula() {
        Valoracion v = new Valoracion(usuario1, pelicula1, 10, "Obra maestra");
        valoracionRepository.save(v);

        Optional<Valoracion> opt = valoracionRepository.findByUsuario_IdUsuarioAndPelicula_IdPelicula(
                usuario1.getIdUsuario(), pelicula1.getIdPelicula());

        assertTrue(opt.isPresent());
        Valoracion encontrada = opt.get();
        assertEquals(10, encontrada.getPuntuacion());
        assertEquals("Obra maestra", encontrada.getComentario());

        // Par inexistente -> Optional vacío
        Optional<Valoracion> optNo = valoracionRepository.findByUsuario_IdUsuarioAndPelicula_IdPelicula(999L, 999L);
        assertFalse(optNo.isPresent());
    }

    @Test
    void testCrudValoracion() {
        Valoracion v = new Valoracion(usuario1, pelicula2, 3, "No me gustó");
        Valoracion guardada = valoracionRepository.save(v);

        assertNotNull(guardada.getIdValoracion());

        Optional<Valoracion> recuperadaOpt = valoracionRepository.findById(guardada.getIdValoracion());
        assertTrue(recuperadaOpt.isPresent());
        Valoracion recuperada = recuperadaOpt.get();
        assertEquals(3, recuperada.getPuntuacion());
        assertEquals("No me gustó", recuperada.getComentario());

        // Comprobar que la película tiene asociada la valoración por id antes de eliminar
        List<Valoracion> valsAntes = valoracionRepository.findByPelicula_IdPelicula(pelicula2.getIdPelicula());
        assertNotNull(valsAntes);
        assertTrue(valsAntes.stream().anyMatch(x -> x.getIdValoracion().equals(guardada.getIdValoracion())));

        // Comprobar que el usuario tiene asociada la valoración por id antes de eliminar
        List<Valoracion> valsUsuarioAntes = valoracionRepository.findByUsuario_IdUsuario(usuario1.getIdUsuario());
        assertNotNull(valsUsuarioAntes);
        assertTrue(valsUsuarioAntes.stream().anyMatch(x -> x.getIdValoracion().equals(guardada.getIdValoracion())));

        // Actualizar
        recuperada.setPuntuacion(5);
        recuperada.setComentario("Mejoró al final");
        valoracionRepository.save(recuperada);

        Optional<Valoracion> actualizadaOpt = valoracionRepository.findById(guardada.getIdValoracion());
        assertTrue(actualizadaOpt.isPresent());
        Valoracion actualizada = actualizadaOpt.get();
        assertEquals(5, actualizada.getPuntuacion());
        assertEquals("Mejoró al final", actualizada.getComentario());

        // Eliminar
        valoracionRepository.delete(actualizada);
        Optional<Valoracion> trasBorrado = valoracionRepository.findById(guardada.getIdValoracion());
        assertFalse(trasBorrado.isPresent());

        // Comprobar que la película ya no tiene asociada la valoración (por id)
        List<Valoracion> valsDespues = valoracionRepository.findByPelicula_IdPelicula(pelicula2.getIdPelicula());
        assertNotNull(valsDespues);
        assertFalse(valsDespues.stream().anyMatch(x -> x.getIdValoracion().equals(guardada.getIdValoracion())));

        // Comprobar que el usuario ya no tiene asociada la valoración (por id)
        List<Valoracion> valsUsuarioDespues = valoracionRepository.findByUsuario_IdUsuario(usuario1.getIdUsuario());
        assertNotNull(valsUsuarioDespues);
        assertFalse(valsUsuarioDespues.stream().anyMatch(x -> x.getIdValoracion().equals(guardada.getIdValoracion())));
    }
}
