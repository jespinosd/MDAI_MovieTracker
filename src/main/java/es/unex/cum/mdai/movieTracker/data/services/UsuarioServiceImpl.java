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
        // Validar que username no sea null o vacío
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }

        // Validar que password no sea null o vacío
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        Usuario usuario = usuarioRepository.findByUsernameAndPassword(username, password);
        return Optional.ofNullable(usuario);
    }

    // Registro
    @Override
    @Transactional
    public Usuario registrar(Usuario usuario) {
        // Validar que el usuario no sea null
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        // Validar nombre
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validar apellido1
        if (usuario.getApellido1() == null || usuario.getApellido1().trim().isEmpty()) {
            throw new IllegalArgumentException("El primer apellido es obligatorio");
        }

        // Validar username
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }

        // Validar email
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        // Validar formato de email
        if (!usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email debe contener '@'");
        }

        // Validación más completa del formato de email
        if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        // Validar password
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        // Validar longitud mínima de contraseña
        if (usuario.getPassword().length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }

        // Validar que no exista el username
        if (existeUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }

        // Validar que no exista el email
        if (existeEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean existeUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.findByUsername(username) != null;
    }

    @Override
    public boolean existeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return usuarioRepository.findByEmail(email) != null;
    }

    // CRUD básico
    @Override
    public Optional<Usuario> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID debe ser válido");
        }
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

        // Validar nombre
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        // Validar apellido1
        if (usuario.getApellido1() == null || usuario.getApellido1().trim().isEmpty()) {
            throw new IllegalArgumentException("El primer apellido es obligatorio");
        }

        // Validar username
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El username es obligatorio");
        }

        // Validar email
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        // Validar formato de email
        if (!usuario.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email debe contener '@'");
        }

        // Validación más completa del formato de email
        if (!usuario.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("El formato del email no es válido");
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

        // Validar nueva contraseña
        if (nuevaPassword == null || nuevaPassword.isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña es obligatoria");
        }

        // Validar longitud mínima de la nueva contraseña
        if (nuevaPassword.length() < 8) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
        }

        usuario.setPassword(nuevaPassword);
        return usuarioRepository.save(usuario);
    }


    // Búsqueda de usuarios
    @Override
    public Optional<Usuario> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(usuarioRepository.findByUsername(username));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(usuarioRepository.findByEmail(email));
    }

    @Override
    public List<Usuario> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return List.of(); // Lista vacía
        }
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Usuario> buscarPorUsername(String usernamePart) {
        if (usernamePart == null || usernamePart.trim().isEmpty()) {
            return List.of(); // Lista vacía
        }
        return usuarioRepository.findByUsernameContainingIgnoreCase(usernamePart);
    }

    // Validaciones
    @Override
    public boolean validarCredenciales(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        Usuario usuario = usuarioRepository.findByUsernameAndPassword(username, password);
        return usuario != null;
    }
}
