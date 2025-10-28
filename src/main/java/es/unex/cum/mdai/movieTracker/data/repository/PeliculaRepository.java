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
    public List<Pelicula> findByGenero(String genero);

    // Búsquedas por partes de campos (ignore case)
    List<Pelicula> findByTituloContainingIgnoreCase(String tituloPart);
    List<Pelicula> findByGeneroContainingIgnoreCase(String generoPart);
    List<Pelicula> findByTituloContainingIgnoreCaseAndAnio(String tituloPart, int anio);

    // Buscar películas por su puntuación media >= valor dado
    @Query("SELECT p FROM Pelicula p LEFT JOIN p.listaValoraciones v GROUP BY p HAVING COALESCE(AVG(v.puntuacion),0) >= :puntuacion")
    List<Pelicula> findByAverageRatingGreaterThanEqual(@Param("puntuacion") double puntuacion);


    // Revisar de aquí hacia abajo

    // Ordenar todas las películas por la valoración media (desc)
    @Query("SELECT p FROM Pelicula p LEFT JOIN p.listaValoraciones v GROUP BY p ORDER BY AVG(v.puntuacion) DESC")
    List<Pelicula> findAllOrderByAverageRatingDesc();

    // Obtener la valoración media de una película
    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.pelicula.idPelicula = :idPelicula")
    Double findAverageRatingByPeliculaId(@Param("idPelicula") Long idPelicula);

    // Contar valoraciones de una película
    @Query("SELECT COUNT(v) FROM Valoracion v WHERE v.pelicula.idPelicula = :idPelicula")
    Long countValoracionesByPeliculaId(@Param("idPelicula") Long idPelicula);

    // Comprobar existencia por título exacto
    boolean existsByTituloIgnoreCase(String titulo);

}
