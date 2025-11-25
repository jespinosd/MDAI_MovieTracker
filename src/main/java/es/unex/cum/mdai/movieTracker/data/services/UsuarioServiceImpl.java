package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Autenticación
    @Override
    public Optional<Usuario> login(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsernameAndPassword(username, password);
        return Optional.ofNullable(usuario);
    }

    // Registro
    @Override
    @Transactional
    public Usuario registrar(Usuario usuario) {
        // Validar que no exista el username o email
        if (existeUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }
        if (existeEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean existeUsername(String username) {
        return usuarioRepository.findByUsername(username) != null;
    }

    @Override
    public boolean existeEmail(String email) {
        return usuarioRepository.findByEmail(email) != null;
    }

    // CRUD básico
    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // El cascade y orphanRemoval se encargan de eliminar las valoraciones y colección asociadas
        usuarioRepository.deleteById(id);
    }

    // Actualización de perfil
    @Override
    @Transactional
    public Usuario actualizarPerfil(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario());
        if (usuarioExistente.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Verificar que el nuevo username no esté en uso por otro usuario
        Usuario usuarioConUsername = usuarioRepository.findByUsername(usuario.getUsername());
        if (usuarioConUsername != null && !usuarioConUsername.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        // Verificar que el nuevo email no esté en uso por otro usuario
        Usuario usuarioConEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioConEmail != null && !usuarioConEmail.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        return usuarioRepository.save(usuario);
    }

    // Java
    @Override
    @Transactional
    public Usuario cambiarPassword(Long idUsuario, String passwordActual, String nuevaPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getPassword().equals(passwordActual)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(nuevaPassword);
        return usuarioRepository.save(usuario);
    }


    // Búsqueda de usuarios
    @Override
    public Optional<Usuario> findByUsername(String username) {
        return Optional.ofNullable(usuarioRepository.findByUsername(username));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return Optional.ofNullable(usuarioRepository.findByEmail(email));
    }

    @Override
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Usuario> buscarPorUsername(String usernamePart) {
        return usuarioRepository.findByUsernameContainingIgnoreCase(usernamePart);
    }

    // Validaciones
    @Override
    public boolean validarCredenciales(String username, String password) {
        Usuario usuario = usuarioRepository.findByUsernameAndPassword(username, password);
        return usuario != null;
    }
}
