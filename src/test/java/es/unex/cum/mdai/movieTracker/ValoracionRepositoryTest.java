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
    private Valoracion valoracion1;
    private Valoracion valoracion2;
    private Valoracion valoracion3;

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

        // Recuperar las valoraciones insertadas por data.sql
        List<Valoracion> todas = (List<Valoracion>) valoracionRepository.findAll();
        // Deben existir exactamente 3 valoraciones definidas en data.sql
        assertEquals(3, todas.size(), "No se han encontrado las 3 valoraciones esperadas en data.sql");

        // Asignar en base a usuario/pelicula
        for (Valoracion v : todas) {
            if (v.getUsuario().getIdUsuario().equals(usuario1.getIdUsuario()) &&
                    v.getPelicula().getIdPelicula().equals(pelicula1.getIdPelicula())) {
                valoracion1 = v; // Alicia - El Padrino (7)
            } else if (v.getUsuario().getIdUsuario().equals(usuario1.getIdUsuario()) &&
                    v.getPelicula().getIdPelicula().equals(pelicula2.getIdPelicula())) {
                valoracion2 = v; // Alicia - Inception (9)
            } else if (v.getUsuario().getIdUsuario().equals(usuario2.getIdUsuario()) &&
                    v.getPelicula().getIdPelicula().equals(pelicula1.getIdPelicula())) {
                valoracion3 = v; // Juan - El Padrino (5)
            }
        }

        assertNotNull(valoracion1, "valoracion1 no encontrada");
        assertNotNull(valoracion2, "valoracion2 no encontrada");
        assertNotNull(valoracion3, "valoracion3 no encontrada");

        // Comprobaciones básicas de contenido (según lo insertado en data.sql)
        assertEquals(7, valoracion1.getPuntuacion());
        assertEquals("Buena", valoracion1.getComentario());

        assertEquals(9, valoracion2.getPuntuacion());
        assertEquals("Excelente", valoracion2.getComentario());

        assertEquals(5, valoracion3.getPuntuacion());
        assertEquals("Regular", valoracion3.getComentario());
    }

    @Test
    void testFindByUsuario_IdUsuario() {
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

        List<Valoracion> valsPeli1 = valoracionRepository.findByPelicula_IdPelicula(pelicula1.getIdPelicula());
        assertNotNull(valsPeli1);
        assertEquals(2, valsPeli1.size());

        assertEquals(7, valsPeli1.get(0).getPuntuacion());
        assertEquals(5, valsPeli1.get(1).getPuntuacion());

        // Película inexistente -> lista vacía
        List<Valoracion> valsNoPeli = valoracionRepository.findByPelicula_IdPelicula(999L);
        assertNotNull(valsNoPeli);
        assertTrue(valsNoPeli.isEmpty());
    }

    @Test
    void testFindByUsuarioAndPelicula() {
        Optional<Valoracion> opt = valoracionRepository.findByUsuario_IdUsuarioAndPelicula_IdPelicula(
                usuario1.getIdUsuario(), pelicula1.getIdPelicula());

        assertTrue(opt.isPresent());
        Valoracion encontrada = opt.get();

        assertEquals(7, encontrada.getPuntuacion());
        assertEquals("Buena", encontrada.getComentario());

        // Par inexistente -> Optional vacío
        Optional<Valoracion> optNo = valoracionRepository.findByUsuario_IdUsuarioAndPelicula_IdPelicula(999L, 999L);
        assertFalse(optNo.isPresent());
    }

    @Test
    void testCrudValoracion() {
        // Crear una nueva valoración (no la que vino en data.sql)
        Valoracion v = new Valoracion(usuario2, pelicula2, 3, "No me gustó");
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
        List<Valoracion> valsUsuarioAntes = valoracionRepository.findByUsuario_IdUsuario(usuario2.getIdUsuario());
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
        List<Valoracion> valsUsuarioDespues = valoracionRepository.findByUsuario_IdUsuario(usuario2.getIdUsuario());
        assertNotNull(valsUsuarioDespues);
        assertFalse(valsUsuarioDespues.stream().anyMatch(x -> x.getIdValoracion().equals(guardada.getIdValoracion())));
    }
}
