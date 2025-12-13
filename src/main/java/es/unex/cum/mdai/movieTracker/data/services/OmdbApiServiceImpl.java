package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.dto.OmdbMovieDTO;
import es.unex.cum.mdai.movieTracker.data.dto.OmdbSearchResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Optional;

/**
 * Implementación del servicio para consumir la API de OMDb
 */
@Service
public class OmdbApiServiceImpl implements OmdbApiService {

    private final WebClient webClient;
    private final String apiKey;

    public OmdbApiServiceImpl(
            @Value("${omdb.api.url}") String apiUrl,
            @Value("${omdb.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
        this.apiKey = apiKey;
    }

    @Override
    public Optional<OmdbSearchResponseDTO> searchMovies(String title, Integer page) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return Optional.empty();
            }

            OmdbSearchResponseDTO response = webClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .queryParam("apikey", apiKey)
                                .queryParam("s", title)
                                .queryParam("type", "movie");
                        if (page != null && page > 0) {
                            uriBuilder.queryParam("page", page);
                        }
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .bodyToMono(OmdbSearchResponseDTO.class)
                    .block();

            if (response != null && response.isSuccessful()) {
                return Optional.of(response);
            }
            return Optional.empty();

        } catch (WebClientResponseException e) {
            System.err.println("Error al buscar películas en OMDb: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error inesperado al consultar OMDb: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<OmdbMovieDTO> getMovieByImdbId(String imdbId) {
        try {
            if (imdbId == null || imdbId.trim().isEmpty()) {
                return Optional.empty();
            }

            OmdbMovieDTO movie = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("apikey", apiKey)
                            .queryParam("i", imdbId)
                            .queryParam("plot", "full")
                            .build())
                    .retrieve()
                    .bodyToMono(OmdbMovieDTO.class)
                    .block();

            if (movie != null && movie.isSuccessful()) {
                return Optional.of(movie);
            }
            return Optional.empty();

        } catch (WebClientResponseException e) {
            System.err.println("Error al obtener película de OMDb: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error inesperado al consultar OMDb: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<OmdbMovieDTO> getMovieByTitle(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return Optional.empty();
            }

            OmdbMovieDTO movie = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("apikey", apiKey)
                            .queryParam("t", title)
                            .queryParam("plot", "full")
                            .build())
                    .retrieve()
                    .bodyToMono(OmdbMovieDTO.class)
                    .block();

            if (movie != null && movie.isSuccessful()) {
                return Optional.of(movie);
            }
            return Optional.empty();

        } catch (WebClientResponseException e) {
            System.err.println("Error al obtener película de OMDb: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error inesperado al consultar OMDb: " + e.getMessage());
            return Optional.empty();
        }
    }
}
