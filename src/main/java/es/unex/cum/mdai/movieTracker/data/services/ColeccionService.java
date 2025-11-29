package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import java.util.List;
import java.util.Optional;

public interface ColeccionService {

    // CRUD básico
    Optional<Coleccion> findById(Long id);
    List<Coleccion> findAll();
    Coleccion save(Coleccion coleccion);
    void deleteById(Long id);

    // Obtener colección de un usuario
    Optional<Coleccion> findByUsuario(Long idUsuario);

    // Crear colección para un usuario
    Coleccion crearColeccionParaUsuario(Long idUsuario);

    // Verificar si usuario tiene colección
    boolean usuarioTieneColeccion(Long idUsuario);

    // Gestión de películas en la colección
    Coleccion agregarPeliculaAColeccion(Long idUsuario, Long idPelicula);
    Coleccion eliminarPeliculaDeColeccion(Long idUsuario, Long idPelicula);
    boolean peliculaEstaEnColeccion(Long idUsuario, Long idPelicula);

    // Obtener películas de la colección del usuario
    List<Pelicula> obtenerPeliculasDeColeccion(Long idUsuario);

    // Obtener colecciones que contienen una película
    List<Coleccion> findColeccionesConPelicula(Long idPelicula);
}
