package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.repository.PeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PeliculaServiceImpl implements PeliculaService {

    private final PeliculaRepository peliculaRepository;

    @Autowired
    public PeliculaServiceImpl(PeliculaRepository peliculaRepository) {
        this.peliculaRepository = peliculaRepository;
    }

    // CRUD básico
    @Override
    public Optional<Pelicula> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }
        return peliculaRepository.findById(id);
    }

    @Override
    public List<Pelicula> findAll() {
        return (List<Pelicula>) peliculaRepository.findAll();
    }

    @Override
    @Transactional
    public Pelicula save(Pelicula pelicula) {
        if (pelicula == null) {
            throw new IllegalArgumentException("La película no puede ser null");
        }

        // Validar título
        if (pelicula.getTitulo() == null || pelicula.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }

        // Validar año
        if (pelicula.getAnio() <= 0) {
            throw new IllegalArgumentException("El año debe ser válido");
        }

        // Validar director
        if (pelicula.getDirector() == null || pelicula.getDirector().trim().isEmpty()) {
            throw new IllegalArgumentException("El director es obligatorio");
        }

        // Validar género
        if (pelicula.getGenero() == null || pelicula.getGenero().trim().isEmpty()) {
            throw new IllegalArgumentException("El género es obligatorio");
        }

        return peliculaRepository.save(pelicula);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }
        // El cascade se encarga de eliminar las valoraciones asociadas
        peliculaRepository.deleteById(id);
    }

    // Búsquedas
    @Override
    public Optional<Pelicula> findByTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(peliculaRepository.findByTitulo(titulo));
    }

    @Override
    public List<Pelicula> findByAnio(int anio) {
        return peliculaRepository.findByAnio(anio);
    }

    @Override
    public List<Pelicula> findByDirector(String director) {
        if (director == null || director.trim().isEmpty()) {
            return List.of();
        }
        return peliculaRepository.findByDirector(director);
    }

    @Override
    public List<Pelicula> findByGenero(String genero) {
        if (genero == null || genero.trim().isEmpty()) {
            return List.of();
        }
        return peliculaRepository.findByGenero(genero);
    }

    // Búsquedas parciales
    @Override
    public List<Pelicula> buscarPorTitulo(String tituloPart) {
        if (tituloPart == null || tituloPart.trim().isEmpty()) {
            return List.of();
        }
        return peliculaRepository.findByTituloContainingIgnoreCase(tituloPart);
    }

    @Override
    public List<Pelicula> buscarPorDirector(String directorPart) {
        if (directorPart == null || directorPart.trim().isEmpty()) {
            return List.of();
        }
        return peliculaRepository.findByDirectorContainingIgnoreCase(directorPart);
    }

    @Override
    public List<Pelicula> buscarPorGenero(String generoPart) {
        if (generoPart == null || generoPart.trim().isEmpty()) {
            return List.of();
        }
        return peliculaRepository.findByGeneroContainingIgnoreCase(generoPart);
    }

    // Búsquedas combinadas
    @Override
    public List<Pelicula> buscar(String query, String tipo) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }

        return switch (tipo != null ? tipo : "titulo") {
            case "director" -> buscarPorDirector(query);
            case "genero" -> buscarPorGenero(query);
            default -> buscarPorTitulo(query);
        };
    }

    @Override
    public List<Pelicula> filtrarPorGeneroYAnio(String genero, Integer anio) {
        List<Pelicula> peliculas = findAll();

        if (genero != null && !genero.trim().isEmpty() && !"todos".equalsIgnoreCase(genero)) {
            peliculas = peliculas.stream()
                    .filter(p -> p.getGenero().equalsIgnoreCase(genero))
                    .collect(Collectors.toList());
        }

        if (anio != null && anio > 0) {
            peliculas = peliculas.stream()
                    .filter(p -> p.getAnio() == anio)
                    .collect(Collectors.toList());
        }

        return peliculas;
    }

    // Búsqueda por valoración
    @Override
    public List<Pelicula> findByAverageRatingGreaterThanEqual(double puntuacion) {
        return peliculaRepository.findByAverageRatingGreaterThanEqual(puntuacion);
    }

    // Obtener valoración media
    @Override
    public Double obtenerValoracionMedia(Long idPelicula) {
        if (idPelicula == null || idPelicula <= 0) {
            return 0.0;
        }
        Double media = peliculaRepository.findAverageRatingByPeliculaId(idPelicula);
        return media != null ? media : 0.0;
    }

    // Validaciones
    @Override
    public boolean existeTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        return peliculaRepository.findByTitulo(titulo) != null;
    }
}
