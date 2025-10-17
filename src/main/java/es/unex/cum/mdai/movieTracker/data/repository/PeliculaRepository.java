package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import org.springframework.data.repository.CrudRepository;

public interface PeliculaRepository extends CrudRepository<Pelicula,Long> {
}
