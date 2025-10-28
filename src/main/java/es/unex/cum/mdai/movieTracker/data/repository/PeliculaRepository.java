package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeliculaRepository extends CrudRepository<Pelicula,Long> {

    // Búsquedas por campos exactos
    public Pelicula findByIdPelicula(Long idPelicula);
    public Pelicula findByTitulo(String titulo);
    public List<Pelicula> findByAnio(int anio);
    public List<Pelicula> findByDirector(String director);
    public List<Pelicula> findByGenero(String genero);

    // Búsquedas por partes de campos (ignore case)
    List<Pelicula> findByTituloContainingIgnoreCase(String tituloPart);
    List<Pelicula> findByDirectorContainingIgnoreCase(String directorPart);
    List<Pelicula> findByGeneroContainingIgnoreCase(String generoPart);
    List<Pelicula> findByTituloContainingIgnoreCaseAndAnio(String tituloPart, int anio);
    List<Pelicula> findByTituloContainingIgnoreCaseAndDirectorContainingIgnoreCase(String tituloPart, String directorPart);

    // Buscar películas por su puntuación media >= valor dado
    @Query("SELECT p FROM Pelicula p LEFT JOIN p.listaValoraciones v GROUP BY p HAVING COALESCE(AVG(v.puntuacion),0) >= :puntuacion")
    List<Pelicula> findByAverageRatingGreaterThanEqual(@Param("puntuacion") double puntuacion);

    // Obtener la valoración media de una película
    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.pelicula.idPelicula = :idPelicula")
    Double findAverageRatingByPeliculaId(@Param("idPelicula") Long idPelicula);

}
