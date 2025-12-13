package es.unex.cum.mdai.movieTracker.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO para mapear la respuesta de búsqueda de la API de OMDb (cuando se buscan múltiples películas)
 */
public class OmdbSearchResponseDTO {

    @JsonProperty("Search")
    private List<OmdbSearchItemDTO> search;

    @JsonProperty("totalResults")
    private String totalResults;

    @JsonProperty("Response")
    private String response;

    @JsonProperty("Error")
    private String error;

    // Constructor por defecto
    public OmdbSearchResponseDTO() {
    }

    // Getters y Setters
    public List<OmdbSearchItemDTO> getSearch() {
        return search;
    }

    public void setSearch(List<OmdbSearchItemDTO> search) {
        this.search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Verifica si la respuesta fue exitosa
     */
    public boolean isSuccessful() {
        return "True".equalsIgnoreCase(response);
    }

    /**
     * Clase interna para representar cada item en los resultados de búsqueda
     */
    public static class OmdbSearchItemDTO {
        @JsonProperty("Title")
        private String title;

        @JsonProperty("Year")
        private String year;

        @JsonProperty("imdbID")
        private String imdbId;

        @JsonProperty("Type")
        private String type;

        @JsonProperty("Poster")
        private String poster;

        // Constructor por defecto
        public OmdbSearchItemDTO() {
        }

        // Getters y Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getImdbId() {
            return imdbId;
        }

        public void setImdbId(String imdbId) {
            this.imdbId = imdbId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPoster() {
            return poster;
        }

        public void setPoster(String poster) {
            this.poster = poster;
        }
    }
}

