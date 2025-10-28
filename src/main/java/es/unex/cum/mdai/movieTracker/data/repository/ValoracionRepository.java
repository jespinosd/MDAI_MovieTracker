package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ValoracionRepository extends CrudRepository<Valoracion,Long> {

    // Buscar todas las valoraciones hechas por un usuario (por id de usuario)
    List<Valoracion> findByUsuario_IdUsuario(Long idUsuario);

    // Buscar todas las valoraciones de una película (por id de película)
    List<Valoracion> findByPelicula_IdPelicula(Long idPelicula);

    // Comprobar si un usuario ya valoró una película y obtener esa valoración (si existe)
    Optional<Valoracion> findByUsuario_IdUsuarioAndPelicula_IdPelicula(Long idUsuario, Long idPelicula);

}
