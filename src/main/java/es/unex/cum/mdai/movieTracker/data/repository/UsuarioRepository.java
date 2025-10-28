package es.unex.cum.mdai.movieTracker.data.repository;

import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    // Autenticación y registro de usuarios
    public Usuario findByIdUsuario(Long idUsuario);
    public Usuario findByNombre(String nombre);
    public Usuario findByUsername(String username);
    public Usuario findByEmail(String email);
    public Usuario findByUsernameAndPassword(String username, String password);

    // Búsqueda de usuarios
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByUsernameContainingIgnoreCase(String usernamePart);

}
