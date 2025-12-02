package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.repository.ColeccionRepository;
import es.unex.cum.mdai.movieTracker.data.repository.PeliculaRepository;
import es.unex.cum.mdai.movieTracker.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ColeccionServiceImpl implements ColeccionService {

    private final ColeccionRepository coleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PeliculaRepository peliculaRepository;

    @Autowired
    public ColeccionServiceImpl(ColeccionRepository coleccionRepository,
                                UsuarioRepository usuarioRepository,
                                PeliculaRepository peliculaRepository) {
        this.coleccionRepository = coleccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.peliculaRepository = peliculaRepository;
    }

    @Override
    public Optional<Coleccion> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }
        return coleccionRepository.findById(id);
    }

    @Override
    public List<Coleccion> findAll() {
        return (List<Coleccion>) coleccionRepository.findAll();
    }

    @Override
    @Transactional
    public Coleccion save(Coleccion coleccion) {
        if (coleccion == null) {
            throw new IllegalArgumentException("La colección no puede ser null");
        }
        return coleccionRepository.save(coleccion);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }

        if (!coleccionRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una colección con ese ID");
        }

        coleccionRepository.deleteById(id);
    }

    @Override
    public Optional<Coleccion> findByUsuario(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        return coleccionRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public Coleccion crearColeccionParaUsuario(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }

        // Verificar si el usuario ya tiene colección
        if (usuarioTieneColeccion(idUsuario)) {
            throw new IllegalArgumentException("El usuario ya tiene una colección");
        }

        // Buscar el usuario
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Crear nueva colección
        Coleccion coleccion = new Coleccion();
        coleccion.setUsuario(usuario);
        coleccion.setListaPeliculas(new ArrayList<>());

        return coleccionRepository.save(coleccion);
    }

    @Override
    public boolean usuarioTieneColeccion(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            return false;
        }
        return coleccionRepository.existsByUsuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional
    public Coleccion agregarPeliculaAColeccion(Long idUsuario, Long idPelicula) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        if (idPelicula == null || idPelicula <= 0) {
            throw new IllegalArgumentException("El ID de la película debe ser válido");
        }

        // Obtener o crear la colección del usuario
        Coleccion coleccion = findByUsuario(idUsuario)
                .orElseGet(() -> crearColeccionParaUsuario(idUsuario));

        // Buscar la película
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

        // Verificar si la película ya está en la colección
        if (coleccion.getListaPeliculas().contains(pelicula)) {
            throw new IllegalArgumentException("La película ya está en la colección");
        }

        // Agregar la película a la colección
        coleccion.getListaPeliculas().add(pelicula);

        return coleccionRepository.save(coleccion);
    }

    @Override
    @Transactional
    public Coleccion eliminarPeliculaDeColeccion(Long idUsuario, Long idPelicula) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        if (idPelicula == null || idPelicula <= 0) {
            throw new IllegalArgumentException("El ID de la película debe ser válido");
        }

        // Obtener la colección del usuario
        Coleccion coleccion = findByUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene colección"));

        // Buscar la película
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

        // Verificar si la película está en la colección
        if (!coleccion.getListaPeliculas().contains(pelicula)) {
            throw new IllegalArgumentException("La película no está en la colección");
        }

        // Eliminar la película de la colección
        coleccion.getListaPeliculas().remove(pelicula);

        return coleccionRepository.save(coleccion);
    }

    @Override
    public boolean peliculaEstaEnColeccion(Long idUsuario, Long idPelicula) {
        if (idUsuario == null || idUsuario <= 0 || idPelicula == null || idPelicula <= 0) {
            return false;
        }
        return coleccionRepository.existsByUsuario_IdUsuarioAndListaPeliculas_IdPelicula(idUsuario, idPelicula);
    }

    @Override
    public List<Pelicula> obtenerPeliculasDeColeccion(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }

        Optional<Coleccion> coleccionOpt = findByUsuario(idUsuario);

        // Si el usuario no tiene colección, devolver una lista vacía
        if (coleccionOpt.isEmpty()) {
            return new ArrayList<>();
        }

        List<Pelicula> peliculas = coleccionOpt.get().getListaPeliculas();

        // Si la lista de películas es null, devolver una lista vacía
        return peliculas != null ? peliculas : new ArrayList<>();
    }

}
