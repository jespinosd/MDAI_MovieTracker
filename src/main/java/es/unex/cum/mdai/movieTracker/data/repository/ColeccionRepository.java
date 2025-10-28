package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ColeccionRepository extends CrudRepository<Coleccion,Long> {

    // Obtener la colección de un usuario (asumiendo relación 1:1)
    Optional<Coleccion> findByUsuario_IdUsuario(Long idUsuario);

    // Comprobar si un usuario ya tiene colección
    boolean existsByUsuario_IdUsuario(Long idUsuario);

    // Encontrar colecciones que contienen una película concreta
    List<Coleccion> findByListaPeliculas_IdPelicula(Long idPelicula);

    // Comprobar si una colección (de un usuario) contiene una película concreta
    boolean existsByUsuario_IdUsuarioAndListaPeliculas_IdPelicula(Long idUsuario, Long idPelicula);

    // Buscar colección por su id y usuario (seguridad/validación)
    Optional<Coleccion> findByIdColeccionAndUsuario_IdUsuario(Long idColeccion, Long idUsuario);

}
