package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.dto.OmdbMovieDTO;
import es.unex.cum.mdai.movieTracker.data.dto.OmdbSearchResponseDTO;

import java.util.Optional;

/**
 * Interfaz para el servicio de la API de OMDb
 */
public interface OmdbApiService {

    /**
     * Busca películas por título en OMDb
     * @param title Título a buscar
     * @param page Número de página (opcional)
     * @return Respuesta con resultados de búsqueda
     */
    Optional<OmdbSearchResponseDTO> searchMovies(String title, Integer page);

    /**
     * Obtiene detalles completos de una película por su ID de IMDb
     * @param imdbId ID de IMDb de la película
     * @return Detalles completos de la película
     */
    Optional<OmdbMovieDTO> getMovieByImdbId(String imdbId);

    /**
     * Obtiene detalles de una película por título exacto
     * @param title Título exacto de la película
     * @return Detalles completos de la película
     */
    Optional<OmdbMovieDTO> getMovieByTitle(String title);
}

