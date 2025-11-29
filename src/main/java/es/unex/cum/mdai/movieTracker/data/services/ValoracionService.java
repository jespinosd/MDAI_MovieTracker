package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import java.util.List;
import java.util.Optional;

public interface ValoracionService {

    // CRUD básico
    Optional<Valoracion> findById(Long id);
    List<Valoracion> findAll();
    Valoracion save(Valoracion valoracion);
    void deleteById(Long id);

    // Buscar valoraciones por usuario
    List<Valoracion> findByUsuario(Long idUsuario);

    // Buscar valoraciones por película
    List<Valoracion> findByPelicula(Long idPelicula);

    // Buscar valoración específica de un usuario para una película
    Optional<Valoracion> findByUsuarioAndPelicula(Long idUsuario, Long idPelicula);

    // Crear o actualizar valoración
    Valoracion crearOActualizarValoracion(Long idUsuario, Long idPelicula, int puntuacion, String comentario);

    // Validaciones
    boolean usuarioYaValoroPelicula(Long idUsuario, Long idPelicula);

}
