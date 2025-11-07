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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PeliculaRepositoryTest {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    private Pelicula pelicula1;
    private Pelicula pelicula2;
    private Pelicula pelicula3;
    private Pelicula pelicula4;
    private Pelicula pelicula5;
    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {
        // Recuperar las entidades insertadas por data.sql
        pelicula1 = peliculaRepository.findByTitulo("El Padrino");
        pelicula2 = peliculaRepository.findByTitulo("Inception");
        pelicula3 = peliculaRepository.findByTitulo("El Padrino II");
        pelicula4 = peliculaRepository.findByTitulo("Interstellar");
        pelicula5 = peliculaRepository.findByTitulo("El Último Tango en París");

        usuario1 = usuarioRepository.findByUsername("juanp");
        usuario2 = usuarioRepository.findByUsername("alicia");

        // Asegurar que los objetos existen en la BD de test
        assertNotNull(pelicula1, "pelicula1 no encontrada en la BD de test");
        assertNotNull(pelicula2, "pelicula2 no encontrada en la BD de test");
        assertNotNull(pelicula3, "pelicula3 no encontrada en la BD de test");
        assertNotNull(pelicula4, "pelicula4 no encontrada en la BD de test");
        assertNotNull(pelicula5, "pelicula5 no encontrada en la BD de test");
        assertNotNull(usuario1, "usuario1 no encontrado en la BD de test");
        assertNotNull(usuario2, "usuario2 no encontrado en la BD de test");
    }

    // ==================== TESTS DE BÚSQUEDAS EXACTAS ====================

    @Test
    void testFindByIdPelicula() {
        Pelicula encontrada = peliculaRepository.findByIdPelicula(pelicula1.getIdPelicula());

        assertNotNull(encontrada);
        assertEquals("El Padrino", encontrada.getTitulo());
        assertEquals(1972, encontrada.getAnio());

        // No encontrada
        Pelicula noEncontrada = peliculaRepository.findByIdPelicula(999L);
        assertNull(noEncontrada);
    }


    @Test
    void testFindByTitulo() {
        Pelicula encontrada = peliculaRepository.findByTitulo("Inception");

        assertNotNull(encontrada);
        assertEquals("Christopher Nolan", encontrada.getDirector());
        assertEquals(2010, encontrada.getAnio());

        Pelicula noEncontrada = peliculaRepository.findByTitulo("Película Inexistente");
        assertNull(noEncontrada);
    }


    @Test
    void testFindByAnio() {
        List<Pelicula> peliculas = peliculaRepository.findByAnio(2010);

        assertNotNull(peliculas);
        assertEquals(1, peliculas.size());
        assertEquals("Inception", peliculas.get(0).getTitulo());
    }

    @Test
    void testFindByAnio_Multiple() {
        List<Pelicula> peliculas = peliculaRepository.findByAnio(1972);

        assertNotNull(peliculas);
        assertEquals(2, peliculas.size());
        assertEquals("El Padrino", peliculas.get(0).getTitulo());
        assertEquals("El Último Tango en París", peliculas.get(1).getTitulo());
    }

    @Test
    void testFindByDirector() {
        List<Pelicula> peliculas = peliculaRepository.findByDirector("Christopher Nolan");

        assertNotNull(peliculas);
        assertEquals(2, peliculas.size());
        assertEquals("Inception", peliculas.get(0).getTitulo());
        assertEquals("Interstellar", peliculas.get(1).getTitulo());

        List<Pelicula> peliculasInexistentes = peliculaRepository.findByDirector("Director Inexistente");
        assertNotNull(peliculasInexistentes);
        assertTrue(peliculasInexistentes.isEmpty());
    }

    @Test
    void testFindByGenero() {
        List<Pelicula> peliculas = peliculaRepository.findByGenero("Drama");

        assertNotNull(peliculas);
        assertEquals(3, peliculas.size());
        assertEquals("El Padrino", peliculas.get(0).getTitulo());
        assertEquals("El Padrino II", peliculas.get(1).getTitulo());
        assertEquals("El Último Tango en París", peliculas.get(2).getTitulo());

        List<Pelicula> peliculasInexistentes = peliculaRepository.findByGenero("Género Inexistente");
        assertNotNull(peliculasInexistentes);
        assertTrue(peliculasInexistentes.isEmpty());
    }

    // ==================== TESTS DE BÚSQUEDAS PARCIALES (IGNORE CASE) ====================

    @Test
    void testFindByTituloContainingIgnoreCase() {
        List<Pelicula> peliculas = peliculaRepository.findByTituloContainingIgnoreCase("padrino");

        assertNotNull(peliculas);
        assertEquals(2, peliculas.size());
        assertEquals("El Padrino", peliculas.get(0).getTitulo());
        assertEquals("El Padrino II", peliculas.get(1).getTitulo());

        List<Pelicula> peliculasMayusculas = peliculaRepository.findByTituloContainingIgnoreCase("INCEPTION");

        assertNotNull(peliculasMayusculas);
        assertEquals(1, peliculasMayusculas.size());
        assertEquals("Inception", peliculasMayusculas.get(0).getTitulo());
    }

    @Test
    void testFindByDirectorContainingIgnoreCase() {
        List<Pelicula> peliculas = peliculaRepository.findByDirectorContainingIgnoreCase("nolan");

        assertNotNull(peliculas);
        assertEquals(2, peliculas.size());

        List<Pelicula> peliculasMayusculas = peliculaRepository.findByDirectorContainingIgnoreCase("COPPOLA");

        assertNotNull(peliculasMayusculas);
        assertEquals(2, peliculasMayusculas.size());
    }

    @Test
    void testFindByGeneroContainingIgnoreCase() {
        List<Pelicula> peliculas = peliculaRepository.findByGeneroContainingIgnoreCase("drama");

        assertNotNull(peliculas);
        assertEquals(3, peliculas.size());

        List<Pelicula> peliculasMayusculas = peliculaRepository.findByGeneroContainingIgnoreCase("CIENCIA");

        assertNotNull(peliculasMayusculas);
        assertEquals(2, peliculasMayusculas.size());
    }


    @Test
    void testFindByTituloContainingIgnoreCaseAndAnio() {
        List<Pelicula> peliculas = peliculaRepository.findByTituloContainingIgnoreCaseAndAnio("padrino", 1972);

        assertNotNull(peliculas);
        assertEquals(1, peliculas.size());
        assertEquals("El Padrino", peliculas.get(0).getTitulo());

        List<Pelicula> peliculasInexistentes = peliculaRepository.findByTituloContainingIgnoreCaseAndAnio("padrino", 2020);

        assertNotNull(peliculasInexistentes);
        assertTrue(peliculasInexistentes.isEmpty());

        List<Pelicula> peliculasMayusculas = peliculaRepository.findByTituloContainingIgnoreCaseAndAnio("INCEPTION", 2010);

        assertNotNull(peliculasMayusculas);
        assertEquals(1, peliculasMayusculas.size());
        assertEquals("Inception", peliculasMayusculas.get(0).getTitulo());
    }

    @Test
    void testFindByTituloContainingIgnoreCaseAndDirectorContainingIgnoreCase() {
        List<Pelicula> peliculas = peliculaRepository
                .findByTituloContainingIgnoreCaseAndDirectorContainingIgnoreCase("padrino", "coppola");

        assertNotNull(peliculas);
        assertEquals(2, peliculas.size());
        assertEquals("El Padrino", peliculas.get(0).getTitulo());
        assertEquals("El Padrino II", peliculas.get(1).getTitulo());

        List<Pelicula> peliculasInexistentes = peliculaRepository
                .findByTituloContainingIgnoreCaseAndDirectorContainingIgnoreCase("padrino", "nolan");

        assertNotNull(peliculasInexistentes);
        assertTrue(peliculasInexistentes.isEmpty());

        List<Pelicula> peliculasMayusculas = peliculaRepository
                .findByTituloContainingIgnoreCaseAndDirectorContainingIgnoreCase("INCEPTION", "NOLAN");
        assertNotNull(peliculasMayusculas);
        assertEquals(1, peliculasMayusculas.size());
        assertEquals("Inception", peliculasMayusculas.get(0).getTitulo());

    }

    // ==================== TESTS DE VALORACIONES ====================
    @Test
    void testFindAverageRatingByPeliculaId() {
        Double mediaInexistente = peliculaRepository.findAverageRatingByPeliculaId(pelicula5.getIdPelicula());
        assertNull(mediaInexistente);

        // Crear valoraciones para pelicula1
        Valoracion val1 = new Valoracion(usuario1, pelicula5, 8, "Excelente película");
        Valoracion val2 = new Valoracion(usuario2, pelicula5, 9, "Obra maestra");
        valoracionRepository.save(val1);
        valoracionRepository.save(val2);

        Double average = peliculaRepository.findAverageRatingByPeliculaId(pelicula5.getIdPelicula());

        assertNotNull(average);
        assertEquals(8.5, average, 0.01);
    }

    @Test
    void testFindByAverageRatingGreaterThanEqual() {
        // Valoraciones para pelicula1 (promedio: 7.33 porque existe una tercera valoración, de 5, en data.sql)
        valoracionRepository.save(new Valoracion(usuario1, pelicula1, 8, "Muy buena"));
        valoracionRepository.save(new Valoracion(usuario2, pelicula1, 9, "Excelente"));

        // Valoraciones para pelicula2 (promedio: 6.0)
        valoracionRepository.save(new Valoracion(usuario1, pelicula2, 6, "Buena"));

        // Valoraciones para pelicula3 (promedio: 9.0)
        valoracionRepository.save(new Valoracion(usuario1, pelicula3, 9, "Obra maestra"));

        // Buscar películas con promedio >= 8.0
        List<Pelicula> peliculas = peliculaRepository.findByAverageRatingGreaterThanEqual(8.0);

        assertNotNull(peliculas);
        assertEquals(1, peliculas.size());
        assertEquals("El Padrino II", peliculas.get(0).getTitulo());

        // Buscar películas con promedio >= 9.5
        List<Pelicula> peliculasValoracionAlta = peliculaRepository.findByAverageRatingGreaterThanEqual(9.5);

        assertNotNull(peliculasValoracionAlta);
        assertTrue(peliculasValoracionAlta.isEmpty());
    }

    // ==================== TESTS ADICIONALES ====================

    @Test
    void testGuardarYRecuperarPelicula() {
        Pelicula nuevaPelicula = new Pelicula(
                "Matrix",
                1999,
                "Lana Wachowski",
                "Acción",
                "Un hacker descubre la verdad sobre la realidad",
                "/img/matrix.jpg"
        );

        Pelicula guardada = peliculaRepository.save(nuevaPelicula);
        assertNotNull(guardada.getIdPelicula());

        Pelicula recuperada = peliculaRepository.findByIdPelicula(guardada.getIdPelicula());
        assertNotNull(recuperada);
        assertEquals("Matrix", recuperada.getTitulo());
        assertEquals("Lana Wachowski", recuperada.getDirector());
    }

    @Test
    void testActualizarPelicula() {
        pelicula1.setSinopsis("Nueva sinopsis actualizada");
        peliculaRepository.save(pelicula1);

        Pelicula actualizada = peliculaRepository.findByIdPelicula(pelicula1.getIdPelicula());
        assertEquals("Nueva sinopsis actualizada", actualizada.getSinopsis());
    }

    @Test
    void testEliminarPelicula() {
        // Crear valoraciones asociadas a pelicula1
        Valoracion val1 = new Valoracion(usuario1, pelicula1, 8, "Muy buena película");
        Valoracion val2 = new Valoracion(usuario2, pelicula1, 9, "Obra maestra");
        valoracionRepository.save(val1);
        valoracionRepository.save(val2);

        // Guardar los IDs antes de eliminar
        Long idPelicula = pelicula1.getIdPelicula();
        Long idVal1 = val1.getIdValoracion();
        Long idVal2 = val2.getIdValoracion();

        // Verificar que las valoraciones existen antes de eliminar
        assertTrue(valoracionRepository.findById(idVal1).isPresent());
        assertTrue(valoracionRepository.findById(idVal2).isPresent());

        // Eliminar la película
        peliculaRepository.delete(pelicula1);

        // Verificar que la película ha sido eliminada
        Pelicula eliminada = peliculaRepository.findByIdPelicula(idPelicula);
        assertNull(eliminada);

        // Verificar que las valoraciones también han sido eliminadas
        assertFalse(valoracionRepository.findById(idVal1).isPresent());
        assertFalse(valoracionRepository.findById(idVal2).isPresent());
    }
}
