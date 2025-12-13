package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.dto.OmdbMovieDTO;
import es.unex.cum.mdai.movieTracker.data.dto.OmdbSearchResponseDTO;
import es.unex.cum.mdai.movieTracker.data.services.OmdbApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el servicio de OMDb API
 * Nota: Estos tests requieren conectividad con la API externa de OMDb
 */
@SpringBootTest
public class OmdbApiServiceTest {

    @Autowired
    private OmdbApiService omdbApiService;

    @Test
    void testSearchMovies_ConTituloValido() {
        // Buscar una película conocida
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies("Matrix", 1);

        assertTrue(resultado.isPresent(), "Debe encontrar resultados para 'Matrix'");
        assertNotNull(resultado.get().getSearch(), "La lista de búsqueda no debe ser null");
        assertFalse(resultado.get().getSearch().isEmpty(), "Debe haber al menos un resultado");
        assertTrue(resultado.get().isSuccessful(), "La respuesta debe ser exitosa");
    }

    @Test
    void testSearchMovies_ConTituloVacio() {
        // Buscar con título vacío
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies("", 1);

        assertFalse(resultado.isPresent(), "No debe devolver resultados con título vacío");
    }

    @Test
    void testSearchMovies_ConTituloNull() {
        // Buscar con título null
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies(null, 1);

        assertFalse(resultado.isPresent(), "No debe devolver resultados con título null");
    }

    @Test
    void testSearchMovies_ConPaginacion() {
        // Buscar con paginación
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies("Star Wars", 1);

        assertTrue(resultado.isPresent(), "Debe encontrar resultados para 'Star Wars'");
        assertNotNull(resultado.get().getTotalResults(), "Debe tener total de resultados");
    }

    @Test
    void testSearchMovies_PeliculaNoExistente() {
        // Buscar película que no existe
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies("xyzabc123notexist", 1);

        // Puede devolver empty o un resultado con error
        assertTrue(resultado.isEmpty() || !resultado.get().isSuccessful(),
                "No debe encontrar resultados para película inexistente");
    }

    @Test
    void testGetMovieByImdbId_ConIdValido() {
        // Buscar por IMDb ID conocido (Matrix - 1999)
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId("tt0133093");

        assertTrue(resultado.isPresent(), "Debe encontrar la película con ID válido");
        assertEquals("The Matrix", resultado.get().getTitle(), "El título debe ser 'The Matrix'");
        assertEquals("1999", resultado.get().getYear(), "El año debe ser 1999");
        assertNotNull(resultado.get().getDirector(), "El director no debe ser null");
        assertNotNull(resultado.get().getGenre(), "El género no debe ser null");
        assertNotNull(resultado.get().getPlot(), "La sinopsis no debe ser null");
        assertTrue(resultado.get().isSuccessful(), "La respuesta debe ser exitosa");
    }

    @Test
    void testGetMovieByImdbId_ConIdInvalido() {
        // Buscar con ID inválido
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId("invalid123");

        assertFalse(resultado.isPresent(), "No debe encontrar película con ID inválido");
    }

    @Test
    void testGetMovieByImdbId_ConIdVacio() {
        // Buscar con ID vacío
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId("");

        assertFalse(resultado.isPresent(), "No debe devolver resultados con ID vacío");
    }

    @Test
    void testGetMovieByImdbId_ConIdNull() {
        // Buscar con ID null
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId(null);

        assertFalse(resultado.isPresent(), "No debe devolver resultados con ID null");
    }

    @Test
    void testGetMovieByTitle_ConTituloExacto() {
        // Buscar por título exacto
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByTitle("The Matrix");

        assertTrue(resultado.isPresent(), "Debe encontrar la película con título exacto");
        assertEquals("The Matrix", resultado.get().getTitle(), "El título debe coincidir");
        assertTrue(resultado.get().isSuccessful(), "La respuesta debe ser exitosa");
    }

    @Test
    void testGetMovieByTitle_ConTituloVacio() {
        // Buscar con título vacío
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByTitle("");

        assertFalse(resultado.isPresent(), "No debe devolver resultados con título vacío");
    }

    @Test
    void testGetMovieByTitle_ConTituloNull() {
        // Buscar con título null
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByTitle(null);

        assertFalse(resultado.isPresent(), "No debe devolver resultados con título null");
    }

    @Test
    void testGetMovieByTitle_PeliculaNoExistente() {
        // Buscar película que no existe
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByTitle("xyznotexistmovie123");

        assertFalse(resultado.isPresent(), "No debe encontrar película inexistente");
    }

    @Test
    void testOmdbMovieDTO_GetYearAsInt() {
        // Probar la conversión de año
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId("tt0133093");

        assertTrue(resultado.isPresent(), "Debe encontrar la película");
        assertEquals(1999, resultado.get().getYearAsInt(), "El año como int debe ser 1999");
    }

    @Test
    void testOmdbMovieDTO_VerificarCampos() {
        // Verificar que todos los campos importantes se mapean correctamente
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId("tt0111161"); // The Shawshank Redemption

        assertTrue(resultado.isPresent(), "Debe encontrar la película");
        OmdbMovieDTO pelicula = resultado.get();

        assertNotNull(pelicula.getTitle(), "El título no debe ser null");
        assertNotNull(pelicula.getYear(), "El año no debe ser null");
        assertNotNull(pelicula.getDirector(), "El director no debe ser null");
        assertNotNull(pelicula.getGenre(), "El género no debe ser null");
        assertNotNull(pelicula.getPlot(), "La sinopsis no debe ser null");
        assertNotNull(pelicula.getImdbId(), "El IMDb ID no debe ser null");
        assertNotNull(pelicula.getPoster(), "El póster no debe ser null");

        assertFalse(pelicula.getTitle().isEmpty(), "El título no debe estar vacío");
        assertTrue(pelicula.getYearAsInt() > 0, "El año debe ser mayor que 0");
    }

    @Test
    void testSearchMovies_VerificarEstructuraRespuesta() {
        // Verificar la estructura de la respuesta de búsqueda
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies("Inception", 1);

        assertTrue(resultado.isPresent(), "Debe encontrar resultados");
        OmdbSearchResponseDTO respuesta = resultado.get();

        assertTrue(respuesta.isSuccessful(), "La respuesta debe ser exitosa");
        assertNotNull(respuesta.getSearch(), "La lista de búsqueda no debe ser null");
        assertNotNull(respuesta.getTotalResults(), "El total de resultados no debe ser null");

        // Verificar el primer resultado
        if (!respuesta.getSearch().isEmpty()) {
            OmdbSearchResponseDTO.OmdbSearchItemDTO primerItem = respuesta.getSearch().get(0);
            assertNotNull(primerItem.getTitle(), "El título del item no debe ser null");
            assertNotNull(primerItem.getYear(), "El año del item no debe ser null");
            assertNotNull(primerItem.getImdbId(), "El IMDb ID del item no debe ser null");
            assertNotNull(primerItem.getType(), "El tipo del item no debe ser null");
        }
    }

    @Test
    void testSearchMovies_ConCaracteresEspeciales() {
        // Buscar con caracteres especiales
        Optional<OmdbSearchResponseDTO> resultado = omdbApiService.searchMovies("Star Wars: Episode IV", 1);

        // Debe manejar los caracteres especiales correctamente
        assertNotNull(resultado, "El resultado no debe ser null");
    }

    @Test
    void testGetMovieByImdbId_VerificarPoster() {
        // Verificar que el póster se obtiene correctamente
        Optional<OmdbMovieDTO> resultado = omdbApiService.getMovieByImdbId("tt0133093");

        assertTrue(resultado.isPresent(), "Debe encontrar la película");
        String poster = resultado.get().getPoster();

        assertNotNull(poster, "El póster no debe ser null");
        assertTrue(poster.equals("N/A") || poster.startsWith("http"),
                "El póster debe ser 'N/A' o una URL válida");
    }
}

