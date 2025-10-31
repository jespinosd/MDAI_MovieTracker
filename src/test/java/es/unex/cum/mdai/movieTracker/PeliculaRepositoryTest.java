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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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

    @BeforeEach
    void setUp() {
        // Limpiar repositorios
        valoracionRepository.deleteAll();
        peliculaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear películas de prueba
        pelicula1 = new Pelicula(
                "El Padrino",
                1972,
                "Francis Ford Coppola",
                "Drama",
                "La historia de la familia Corleone",
                "/images/padrino.jpg"
        );

        pelicula2 = new Pelicula(
                "Inception",
                2010,
                "Christopher Nolan",
                "Ciencia Ficción",
                "Un ladrón que roba secretos a través de los sueños",
                "/images/inception.jpg"
        );

        pelicula3 = new Pelicula(
                "El Padrino II",
                1974,
                "Francis Ford Coppola",
                "Drama",
                "La continuación de la saga Corleone",
                "/images/padrino2.jpg"
        );

        pelicula4 = new Pelicula(
                "Interstellar",
                2014,
                "Christopher Nolan",
                "Ciencia Ficción",
                "Un grupo de astronautas viaja a través de un agujero de gusano",
                "/images/interstellar.jpg"
        );

        pelicula5 = new Pelicula(
                "El Último Tango en París",
                1972,
                "Bernardo Bertolucci",
                "Drama",
                "Una historia de amor y pasión en París",
                "/images/tango.jpg"
        );

        // Guardar películas
        peliculaRepository.save(pelicula1);
        peliculaRepository.save(pelicula2);
        peliculaRepository.save(pelicula3);
        peliculaRepository.save(pelicula4);
        peliculaRepository.save(pelicula5);

        // Crear usuario para valoraciones
        usuario1 = new Usuario("Juan", "Pérez", "García", "juan.perez@test.com", "usuario1", "password123");
        usuarioRepository.save(usuario1);
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
        Double mediaInexistente = peliculaRepository.findAverageRatingByPeliculaId(pelicula1.getIdPelicula());
        assertNull(mediaInexistente);

        // Crear valoraciones para pelicula1
        Valoracion val1 = new Valoracion(usuario1, pelicula1, 8, "Excelente película");
        Valoracion val2 = new Valoracion(usuario1, pelicula1, 9, "Obra maestra");
        valoracionRepository.save(val1);
        valoracionRepository.save(val2);

        Double average = peliculaRepository.findAverageRatingByPeliculaId(pelicula1.getIdPelicula());

        assertNotNull(average);
        assertEquals(8.5, average, 0.01);
    }

    @Test
    void testFindByAverageRatingGreaterThanEqual() {
        List<Pelicula> peliculasSinValoraciones = peliculaRepository.findByAverageRatingGreaterThanEqual(5.0);

        // Sin valoraciones, las películas tienen promedio 0
        assertNotNull(peliculasSinValoraciones);
        assertTrue(peliculasSinValoraciones.isEmpty());

        // Valoraciones para pelicula1 (promedio: 8.5)
        valoracionRepository.save(new Valoracion(usuario1, pelicula1, 8, "Muy buena"));
        valoracionRepository.save(new Valoracion(usuario1, pelicula1, 9, "Excelente"));

        // Valoraciones para pelicula2 (promedio: 6.0)
        valoracionRepository.save(new Valoracion(usuario1, pelicula2, 6, "Buena"));

        // Valoraciones para pelicula3 (promedio: 9.0)
        valoracionRepository.save(new Valoracion(usuario1, pelicula3, 9, "Obra maestra"));

        // Buscar películas con promedio >= 8.0
        List<Pelicula> peliculas = peliculaRepository.findByAverageRatingGreaterThanEqual(8.0);

        assertNotNull(peliculas);
        assertEquals(2, peliculas.size());
        assertEquals("El Padrino", peliculas.get(0).getTitulo());
        assertEquals("El Padrino II", peliculas.get(1).getTitulo());

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
                "/images/matrix.jpg"
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
        Long id = pelicula1.getIdPelicula();
        peliculaRepository.delete(pelicula1);

        Pelicula eliminada = peliculaRepository.findByIdPelicula(id);
        assertNull(eliminada);
    }

    @Test
    void testContarPeliculas() {
        long count = peliculaRepository.count();
        assertEquals(5, count);
    }
}
