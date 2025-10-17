package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import org.springframework.data.repository.CrudRepository;

public interface ColeccionRepository extends CrudRepository<Coleccion,Long> {
}
