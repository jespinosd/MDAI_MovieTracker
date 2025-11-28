package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import java.util.List;
import java.util.Optional;

public interface PeliculaService {

    // CRUD básico
    Optional<Pelicula> findById(Long id);
    List<Pelicula> findAll();
    Pelicula save(Pelicula pelicula);
    void deleteById(Long id);

    // Búsquedas
    Optional<Pelicula> findByTitulo(String titulo);
    List<Pelicula> findByAnio(int anio);
    List<Pelicula> findByDirector(String director);
    List<Pelicula> findByGenero(String genero);

    // Búsquedas parciales
    List<Pelicula> buscarPorTitulo(String tituloPart);
    List<Pelicula> buscarPorDirector(String directorPart);
    List<Pelicula> buscarPorGenero(String generoPart);

    // Búsquedas combinadas
    List<Pelicula> buscar(String query, String tipo);
    List<Pelicula> filtrarPorGeneroYAnio(String genero, Integer anio);

    // Búsqueda por valoración
    List<Pelicula> findByAverageRatingGreaterThanEqual(double puntuacion);

    // Obtener valoración media
    Double obtenerValoracionMedia(Long idPelicula);

    // Validaciones
    boolean existeTitulo(String titulo);
}
