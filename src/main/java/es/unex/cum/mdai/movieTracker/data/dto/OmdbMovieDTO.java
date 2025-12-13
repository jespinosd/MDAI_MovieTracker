package es.unex.cum.mdai.movieTracker.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para mapear la respuesta de la API de OMDb para una película individual
 */
public class OmdbMovieDTO {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Director")
    private String director;

    @JsonProperty("Genre")
    private String genre;

    @JsonProperty("Plot")
    private String plot;

    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("imdbID")
    private String imdbId;

    @JsonProperty("Response")
    private String response;

    @JsonProperty("Error")
    private String error;

    // Constructor por defecto
    public OmdbMovieDTO() {
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

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
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
     * Convierte el año de String a int, maneja casos con rangos como "2019-2020"
     */
    public int getYearAsInt() {
        try {
            if (year != null && year.contains("–")) {
                // Si es un rango (series), toma el primer año
                return Integer.parseInt(year.split("–")[0]);
            }
            return Integer.parseInt(year);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Verifica si la respuesta fue exitosa
     */
    public boolean isSuccessful() {
        return "True".equalsIgnoreCase(response);
    }
}
