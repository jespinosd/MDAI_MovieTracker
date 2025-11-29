package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.repository.PeliculaRepository;
import es.unex.cum.mdai.movieTracker.data.repository.UsuarioRepository;
import es.unex.cum.mdai.movieTracker.data.repository.ValoracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ValoracionServiceImpl implements ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PeliculaRepository peliculaRepository;

    @Autowired
    public ValoracionServiceImpl(ValoracionRepository valoracionRepository,
                                  UsuarioRepository usuarioRepository,
                                  PeliculaRepository peliculaRepository) {
        this.valoracionRepository = valoracionRepository;
        this.usuarioRepository = usuarioRepository;
        this.peliculaRepository = peliculaRepository;
    }

    @Override
    public Optional<Valoracion> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }
        return valoracionRepository.findById(id);
    }

    @Override
    public List<Valoracion> findAll() {
        return (List<Valoracion>) valoracionRepository.findAll();
    }

    @Override
    @Transactional
    public Valoracion save(Valoracion valoracion) {
        if (valoracion == null) {
            throw new IllegalArgumentException("La valoración no puede ser null");
        }

        // Validar puntuación (debe estar entre 1 y 10)
        if (valoracion.getPuntuacion() < 1 || valoracion.getPuntuacion() > 10) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 10");
        }

        // Validar que existan usuario y película
        if (valoracion.getUsuario() == null || valoracion.getPelicula() == null) {
            throw new IllegalArgumentException("Usuario y película son obligatorios");
        }

        return valoracionRepository.save(valoracion);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }

        if (!valoracionRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una valoración con ese ID");
        }

        valoracionRepository.deleteById(id);
    }

    @Override
    public List<Valoracion> findByUsuario(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        return valoracionRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Valoracion> findByPelicula(Long idPelicula) {
        if (idPelicula == null || idPelicula <= 0) {
            throw new IllegalArgumentException("El ID de la película debe ser válido");
        }
        return valoracionRepository.findByPelicula_IdPelicula(idPelicula);
    }

    @Override
    public Optional<Valoracion> findByUsuarioAndPelicula(Long idUsuario, Long idPelicula) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        if (idPelicula == null || idPelicula <= 0) {
            throw new IllegalArgumentException("El ID de la película debe ser válido");
        }
        return valoracionRepository.findByUsuario_IdUsuarioAndPelicula_IdPelicula(idUsuario, idPelicula);
    }

    @Override
    @Transactional
    public Valoracion crearOActualizarValoracion(Long idUsuario, Long idPelicula, int puntuacion, String comentario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        if (idPelicula == null || idPelicula <= 0) {
            throw new IllegalArgumentException("El ID de la película debe ser válido");
        }
        if (puntuacion < 1 || puntuacion > 10) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 10");
        }

        // Buscar usuario y película
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada"));

        // Verificar si ya existe una valoración
        Optional<Valoracion> valoracionExistente = findByUsuarioAndPelicula(idUsuario, idPelicula);

        Valoracion valoracion;
        if (valoracionExistente.isPresent()) {
            // Actualizar valoración existente
            valoracion = valoracionExistente.get();
            valoracion.setPuntuacion(puntuacion);
            valoracion.setComentario(comentario);
        } else {
            // Crear nueva valoración
            valoracion = new Valoracion();
            valoracion.setUsuario(usuario);
            valoracion.setPelicula(pelicula);
            valoracion.setPuntuacion(puntuacion);
            valoracion.setComentario(comentario);
        }

        return valoracionRepository.save(valoracion);
    }

    @Override
    public List<Valoracion> obtenerPeliculasVistas(Long idUsuario) {
        if (idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser válido");
        }
        return findByUsuario(idUsuario);
    }

    @Override
    public boolean usuarioYaValoroPelicula(Long idUsuario, Long idPelicula) {
        if (idUsuario == null || idUsuario <= 0 || idPelicula == null || idPelicula <= 0) {
            return false;
        }
        return findByUsuarioAndPelicula(idUsuario, idPelicula).isPresent();
    }

    @Override
    public Double calcularValoracionMedia(Long idPelicula) {
        if (idPelicula == null || idPelicula <= 0) {
            return 0.0;
        }

        List<Valoracion> valoraciones = findByPelicula(idPelicula);

        if (valoraciones.isEmpty()) {
            return 0.0;
        }

        double suma = valoraciones.stream()
                .mapToInt(Valoracion::getPuntuacion)
                .sum();

        return suma / valoraciones.size();
    }
}
