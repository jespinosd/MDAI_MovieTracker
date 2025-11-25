package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    // Autenticación
    Optional<Usuario> login(String username, String password);

    // Registro
    Usuario registrar(Usuario usuario);
    boolean existeUsername(String username);
    boolean existeEmail(String email);

    // CRUD básico
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    // Actualización de perfil
    Usuario actualizarPerfil(Usuario usuario);
    Usuario cambiarPassword(Long idUsuario, String passwordActual, String nuevaPassword);

    // Búsqueda de usuarios
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> buscarPorNombre(String nombre);
    List<Usuario> buscarPorUsername(String usernamePart);

    // Validaciones
    boolean validarCredenciales(String username, String password);
}
