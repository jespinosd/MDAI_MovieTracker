package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.services.PeliculaService;
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
public class PeliculaServiceTest {

    @Autowired
    private PeliculaService peliculaService;

    private Pelicula peliculaExistente;

    @BeforeEach
    void setUp() {
        // Recuperar una película insertada por data.sql
        peliculaExistente = peliculaService.findByTitulo("El Padrino").orElse(null);
        assertNotNull(peliculaExistente, "La película de prueba debe existir");
    }

    @Test
    void testFindById() {
        // ID existe
        Long id = peliculaExistente.getIdPelicula();
        Optional<Pelicula> resultado = peliculaService.findById(id);
        assertTrue(resultado.isPresent(), "Debe encontrar la película");
        assertEquals(id, resultado.get().getIdPelicula());

        // ID no existe
        Long idNoExistente = 99999L;
        resultado = peliculaService.findById(idNoExistente);
        assertFalse(resultado.isPresent(), "No debe encontrar la película");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.findById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.findById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.findById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");
    }

    @Test
    void testFindAll() {
        List<Pelicula> peliculas = peliculaService.findAll();
        assertNotNull(peliculas, "La lista no debe ser null");
        assertFalse(peliculas.isEmpty(), "La lista no debe estar vacía");
        assertTrue(peliculas.size() >= 5, "Debe haber al menos 5 películas de data.sql");
    }

    @Test
    void testSave() {
        // Caso exitoso
        Pelicula nuevaPelicula = new Pelicula(
                "Matrix",
                1999,
                "Wachowski",
                "Ciencia Ficción",
                "Un hacker descubre la verdad sobre la realidad",
                "/img/matrix.jpg"
        );
        Pelicula resultado = peliculaService.save(nuevaPelicula);
        assertNotNull(resultado, "La película debe ser guardada");
        assertNotNull(resultado.getIdPelicula(), "La película debe tener un ID asignado");
        assertEquals("Matrix", resultado.getTitulo());
        assertTrue(peliculaService.existeTitulo("Matrix"));

        // Película null
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(null);
        }, "Debe lanzar excepción cuando la película es null");

        // Título null
        Pelicula peliculaTituloNull = new Pelicula(null, 2000, "Director", "Drama", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaTituloNull);
        }, "Debe lanzar excepción cuando el título es null");

        // Título vacío
        Pelicula peliculaTituloVacio = new Pelicula("", 2000, "Director", "Drama", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaTituloVacio);
        }, "Debe lanzar excepción cuando el título está vacío");

        // Año inválido (0)
        Pelicula peliculaAnioInvalido = new Pelicula("Título", 0, "Director", "Drama", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaAnioInvalido);
        }, "Debe lanzar excepción cuando el año es 0");

        // Año negativo
        Pelicula peliculaAnioNegativo = new Pelicula("Título", -1, "Director", "Drama", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaAnioNegativo);
        }, "Debe lanzar excepción cuando el año es negativo");

        // Director null
        Pelicula peliculaDirectorNull = new Pelicula("Título", 2000, null, "Drama", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaDirectorNull);
        }, "Debe lanzar excepción cuando el director es null");

        // Director vacío
        Pelicula peliculaDirectorVacio = new Pelicula("Título", 2000, "", "Drama", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaDirectorVacio);
        }, "Debe lanzar excepción cuando el director está vacío");

        // Género null
        Pelicula peliculaGeneroNull = new Pelicula("Título", 2000, "Director", null, "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaGeneroNull);
        }, "Debe lanzar excepción cuando el género es null");

        // Género vacío
        Pelicula peliculaGeneroVacio = new Pelicula("Título", 2000, "Director", "", "Sinopsis", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaGeneroVacio);
        }, "Debe lanzar excepción cuando el género está vacío");

        // Sinopsis null
        Pelicula peliculaSinopsisNull = new Pelicula("Título", 2000, "Director", "Drama", null, "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaSinopsisNull);
        }, "Debe lanzar excepción cuando la sinopsis es null");

        // Sinopsis vacía
        Pelicula peliculaSinopsisVacia = new Pelicula("Título", 2000, "Director", "Drama", "", "/img/test.jpg");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaSinopsisVacia);
        }, "Debe lanzar excepción cuando la sinopsis está vacía");

        // Path imagen null
        Pelicula peliculaImagenNull = new Pelicula("Título", 2000, "Director", "Drama", "Sinopsis", null);
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaImagenNull);
        }, "Debe lanzar excepción cuando el path de imagen es null");

        // Path imagen vacío
        Pelicula peliculaImagenVacia = new Pelicula("Título", 2000, "Director", "Drama", "Sinopsis", "");
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.save(peliculaImagenVacia);
        }, "Debe lanzar excepción cuando el path de imagen está vacío");
    }

    @Test
    void testDeleteById() {
        // Caso exitoso
        Long id = peliculaExistente.getIdPelicula();
        peliculaService.deleteById(id);
        Optional<Pelicula> resultado = peliculaService.findById(id);
        assertFalse(resultado.isPresent(), "La película debe haber sido eliminada");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.deleteById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.deleteById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            peliculaService.deleteById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");
    }

    @Test
    void testFindByTitulo() {
        // Título existe
        Optional<Pelicula> resultado = peliculaService.findByTitulo("El Padrino");
        assertTrue(resultado.isPresent(), "Debe encontrar la película");
        assertEquals("El Padrino", resultado.get().getTitulo());

        // Título no existe
        resultado = peliculaService.findByTitulo("Película Inexistente");
        assertFalse(resultado.isPresent(), "No debe encontrar la película");

        // Título null
        resultado = peliculaService.findByTitulo(null);
        assertFalse(resultado.isPresent(), "Debe retornar Optional vacío cuando título es null");

        // Título vacío
        resultado = peliculaService.findByTitulo("");
        assertFalse(resultado.isPresent(), "Debe retornar Optional vacío cuando título está vacío");
    }

    @Test
    void testFindByAnio() {
        // Año existe (1972)
        List<Pelicula> resultados = peliculaService.findByAnio(1972);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(p -> p.getAnio() == 1972));

        // Año no existe
        resultados = peliculaService.findByAnio(2050);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Año menor a 1800
        resultados = peliculaService.findByAnio(1799);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Año mayor a 2100
        resultados = peliculaService.findByAnio(2101);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testFindByDirector() {
        // Director existe
        List<Pelicula> resultados = peliculaService.findByDirector("Francis Ford Coppola");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(p -> p.getDirector().equals("Francis Ford Coppola")));

        // Director no existe
        resultados = peliculaService.findByDirector("Director Inexistente");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Director null
        resultados = peliculaService.findByDirector(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Director vacío
        resultados = peliculaService.findByDirector("");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testFindByGenero() {
        // Género existe
        List<Pelicula> resultados = peliculaService.findByGenero("Drama");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(p -> p.getGenero().equals("Drama")));

        // Género no existe
        resultados = peliculaService.findByGenero("Western");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Género null
        resultados = peliculaService.findByGenero(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Género vacío
        resultados = peliculaService.findByGenero("");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testBuscarPorTitulo() {
        // Búsqueda parcial exitosa
        List<Pelicula> resultados = peliculaService.buscarPorTitulo("padrino");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(p -> p.getTitulo().toLowerCase().contains("padrino")));

        // Búsqueda sin resultados
        resultados = peliculaService.buscarPorTitulo("xyz123");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Query null
        resultados = peliculaService.buscarPorTitulo(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Query vacía
        resultados = peliculaService.buscarPorTitulo("");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testBuscarPorDirector() {
        // Búsqueda parcial exitosa
        List<Pelicula> resultados = peliculaService.buscarPorDirector("nolan");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(p -> p.getDirector().toLowerCase().contains("nolan")));

        // Búsqueda sin resultados
        resultados = peliculaService.buscarPorDirector("xyz123");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Query null
        resultados = peliculaService.buscarPorDirector(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Query vacía
        resultados = peliculaService.buscarPorDirector("");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testBuscarPorGenero() {
        // Búsqueda parcial exitosa
        List<Pelicula> resultados = peliculaService.buscarPorGenero("dra");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(p -> p.getGenero().toLowerCase().contains("dra")));

        // Búsqueda sin resultados
        resultados = peliculaService.buscarPorGenero("xyz123");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Query null
        resultados = peliculaService.buscarPorGenero(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Query vacía
        resultados = peliculaService.buscarPorGenero("");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testBuscar() {
        // Búsqueda por título (tipo por defecto)
        List<Pelicula> resultados = peliculaService.buscar("padrino", "titulo");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());

        // Búsqueda por director
        resultados = peliculaService.buscar("nolan", "director");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());

        // Búsqueda por género
        resultados = peliculaService.buscar("drama", "genero");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());

        // Búsqueda sin tipo especificado (default: título)
        resultados = peliculaService.buscar("padrino", null);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());

        // Query vacía retorna todas las películas
        resultados = peliculaService.buscar("", "titulo");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertEquals(peliculaService.findAll().size(), resultados.size());

        // Query null retorna todas las películas
        resultados = peliculaService.buscar(null, "titulo");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertEquals(peliculaService.findAll().size(), resultados.size());
    }

    @Test
    void testFiltrarPorGeneroYAnio() {
        // Filtrar por género y año
        List<Pelicula> resultados = peliculaService.filtrarPorGeneroYAnio("Drama", 1972);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(p -> p.getGenero().equals("Drama") && p.getAnio() == 1972));

        // Filtrar solo por género
        resultados = peliculaService.filtrarPorGeneroYAnio("Drama", null);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(p -> p.getGenero().equals("Drama")));

        // Filtrar solo por año
        resultados = peliculaService.filtrarPorGeneroYAnio(null, 1972);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().allMatch(p -> p.getAnio() == 1972));

        // Sin filtros (retorna todas)
        resultados = peliculaService.filtrarPorGeneroYAnio(null, null);
        assertNotNull(resultados);
        assertEquals(peliculaService.findAll().size(), resultados.size());

        // Género "todos" retorna todas
        resultados = peliculaService.filtrarPorGeneroYAnio("todos", null);
        assertNotNull(resultados);
        assertEquals(peliculaService.findAll().size(), resultados.size());

        // Año inválido (menor a 1800) se ignora
        resultados = peliculaService.filtrarPorGeneroYAnio("Drama", 1799);
        assertNotNull(resultados);
        assertTrue(resultados.stream().allMatch(p -> p.getGenero().equals("Drama")));

        // Año inválido (mayor a 2100) se ignora
        resultados = peliculaService.filtrarPorGeneroYAnio("Drama", 2101);
        assertNotNull(resultados);
        assertTrue(resultados.stream().allMatch(p -> p.getGenero().equals("Drama")));

        // Año 0 se ignora
        resultados = peliculaService.filtrarPorGeneroYAnio("Drama", 0);
        assertNotNull(resultados);
        assertTrue(resultados.stream().allMatch(p -> p.getGenero().equals("Drama")));
    }

    @Test
    void testFindByAverageRatingGreaterThanEqual() {
        // Películas con valoración >= 9.0 (solo Inception con media 9.0)
        List<Pelicula> resultados = peliculaService.findByAverageRatingGreaterThanEqual(9.0);
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        assertEquals("Inception", resultados.get(0).getTitulo());

        // Películas con valoración >= 6.0 (El Padrino con media 6.0 e Inception con 9.0)
        resultados = peliculaService.findByAverageRatingGreaterThanEqual(6.0);
        assertNotNull(resultados);
        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().anyMatch(p -> p.getTitulo().equals("El Padrino")));
        assertTrue(resultados.stream().anyMatch(p -> p.getTitulo().equals("Inception")));

        // Películas con valoración >= 5.0 (las que tienen valoraciones)
        resultados = peliculaService.findByAverageRatingGreaterThanEqual(5.0);
        assertNotNull(resultados);
        assertTrue(resultados.size() >= 2);

        // Puntuación muy alta (10.0) - ninguna película alcanza esta media
        resultados = peliculaService.findByAverageRatingGreaterThanEqual(10.0);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Puntuación 0 (válida) - retorna todas las películas con valoraciones
        resultados = peliculaService.findByAverageRatingGreaterThanEqual(0.0);
        assertNotNull(resultados);
        assertTrue(resultados.size() >= 2);

        // Puntuación negativa (inválida)
        resultados = peliculaService.findByAverageRatingGreaterThanEqual(-1.0);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Puntuación mayor a 10 (inválida)
        resultados = peliculaService.findByAverageRatingGreaterThanEqual(11.0);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testObtenerValoracionMedia() {
        // El Padrino tiene 2 valoraciones: 7 (Alicia) y 5 (Juan) → Media: 6.0
        Pelicula elPadrino = peliculaService.findByTitulo("El Padrino").orElseThrow();
        Double mediaPadrino = peliculaService.obtenerValoracionMedia(elPadrino.getIdPelicula());
        assertNotNull(mediaPadrino);
        assertEquals(6.0, mediaPadrino, 0.01);

        // Inception tiene 1 valoración: 9 (Alicia) → Media: 9.0
        Pelicula inception = peliculaService.findByTitulo("Inception").orElseThrow();
        Double mediaInception = peliculaService.obtenerValoracionMedia(inception.getIdPelicula());
        assertNotNull(mediaInception);
        assertEquals(9.0, mediaInception, 0.01);

        // Interstellar no tiene valoraciones → Media: 0.0
        Pelicula interstellar = peliculaService.findByTitulo("Interstellar").orElseThrow();
        Double mediaInterstellar = peliculaService.obtenerValoracionMedia(interstellar.getIdPelicula());
        assertNotNull(mediaInterstellar);
        assertEquals(0.0, mediaInterstellar, 0.01);

        // ID que no existe
        Double mediaInexistente = peliculaService.obtenerValoracionMedia(99999L);
        assertEquals(0.0, mediaInexistente);

        // ID null
        Double mediaNull = peliculaService.obtenerValoracionMedia(null);
        assertEquals(0.0, mediaNull);

        // ID negativo
        Double mediaNegativo = peliculaService.obtenerValoracionMedia(-1L);
        assertEquals(0.0, mediaNegativo);

        // ID cero
        Double mediaCero = peliculaService.obtenerValoracionMedia(0L);
        assertEquals(0.0, mediaCero);
    }

    @Test
    void testExisteTitulo() {
        // Título existe
        assertTrue(peliculaService.existeTitulo("El Padrino"), "Debe retornar true para título existente");

        // Título no existe
        assertFalse(peliculaService.existeTitulo("Película Inexistente"), "Debe retornar false para título no existente");

        // Título null
        assertFalse(peliculaService.existeTitulo(null), "Debe retornar false cuando título es null");

        // Título vacío
        assertFalse(peliculaService.existeTitulo(""), "Debe retornar false cuando título está vacío");
    }
}

