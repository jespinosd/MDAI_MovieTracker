package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario,Long> {
}
