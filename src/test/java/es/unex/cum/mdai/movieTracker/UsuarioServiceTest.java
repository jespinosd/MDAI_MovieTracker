package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Rol;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        // Recuperar un usuario insertado por data.sql
        usuarioExistente = usuarioService.findByUsername("alicia").orElse(null);
        assertNotNull(usuarioExistente, "El usuario de prueba debe existir");
    }

    @Test
    void testLogin() {
        // Caso exitoso
        Optional<Usuario> resultado = usuarioService.login("alicia", "aliciaPass!23");
        assertTrue(resultado.isPresent(), "El login debe ser exitoso");
        assertEquals("alicia", resultado.get().getUsername());

        // Credenciales incorrectas
        resultado = usuarioService.login("alicia", "wrongpassword");
        assertFalse(resultado.isPresent(), "El login debe fallar con credenciales incorrectas");

        // Username null
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login(null, "password123");
        }, "Debe lanzar excepción cuando username es null");

        // Username vacío
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login("", "password123");
        }, "Debe lanzar excepción cuando username está vacío");

        // Password null
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login("alicia", null);
        }, "Debe lanzar excepción cuando password es null");

        // Password vacío
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.login("alicia", "");
        }, "Debe lanzar excepción cuando password está vacío");
    }

    @Test
    void testRegistrar() {
        // Caso exitoso
        Usuario nuevoUsuario = new Usuario(
                "Carlos",
                "González",
                "Pérez",
                "carlos@email.com",
                "carlosg",
                "password123"
        );
        Usuario resultado = usuarioService.registrar(nuevoUsuario);
        assertNotNull(resultado, "El usuario debe ser registrado");
        assertNotNull(resultado.getIdUsuario(), "El usuario debe tener un ID asignado");
        assertEquals("Carlos", resultado.getNombre());
        assertEquals("carlosg", resultado.getUsername());
        assertEquals(Rol.USER, resultado.getRol(), "El rol por defecto debe ser USER");
        assertTrue(usuarioService.existeUsername("carlosg"));

        // Usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(null);
        }, "Debe lanzar excepción cuando el usuario es null");

        // Nombre vacío
        Usuario usuarioNombreVacio = new Usuario("", "González", "Pérez", "test@email.com", "testuser", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioNombreVacio);
        }, "Debe lanzar excepción cuando el nombre está vacío");

        // Apellido1 vacío
        Usuario usuarioApellidoVacio = new Usuario("Carlos", "", "Pérez", "test2@email.com", "testuser2", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioApellidoVacio);
        }, "Debe lanzar excepción cuando el apellido1 está vacío");

        // Username vacío
        Usuario usuarioUsernameVacio = new Usuario("Carlos", "González", "Pérez", "test3@email.com", "", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioUsernameVacio);
        }, "Debe lanzar excepción cuando el username está vacío");

        // Email vacío
        Usuario usuarioEmailVacio = new Usuario("Carlos", "González", "Pérez", "", "testuser3", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioEmailVacio);
        }, "Debe lanzar excepción cuando el email está vacío");

        // Email sin arroba
        Usuario usuarioEmailSinArroba = new Usuario("Carlos", "González", "Pérez", "testemail.com", "testuser4", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioEmailSinArroba);
        }, "Debe lanzar excepción cuando el email no contiene '@'");

        // Email formato inválido
        Usuario usuarioEmailInvalido = new Usuario("Carlos", "González", "Pérez", "test@", "testuser5", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioEmailInvalido);
        }, "Debe lanzar excepción cuando el formato del email es inválido");

        // Password corto
        Usuario usuarioPasswordCorto = new Usuario("Carlos", "González", "Pérez", "test4@email.com", "testuser6", "pass");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioPasswordCorto);
        }, "Debe lanzar excepción cuando la contraseña tiene menos de 8 caracteres");

        // Username duplicado
        Usuario usuarioUsernameDuplicado = new Usuario("Carlos", "González", "Pérez", "nuevo@email.com", "alicia", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioUsernameDuplicado);
        }, "Debe lanzar excepción cuando el username ya existe");

        // Email duplicado
        String emailExistente = usuarioExistente.getEmail();
        Usuario usuarioEmailDuplicado = new Usuario("Carlos", "González", "Pérez", emailExistente, "nuevouser", "password123");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(usuarioEmailDuplicado);
        }, "Debe lanzar excepción cuando el email ya está registrado");
    }

    @Test
    void testExisteUsername() {
        // Username existe
        assertTrue(usuarioService.existeUsername("alicia"), "Debe retornar true para username existente");

        // Username no existe
        assertFalse(usuarioService.existeUsername("usuarioNoExistente"), "Debe retornar false para username no existente");

        // Username null
        assertFalse(usuarioService.existeUsername(null), "Debe retornar false cuando username es null");
    }

    @Test
    void testExisteEmail() {
        // Email existe
        assertTrue(usuarioService.existeEmail(usuarioExistente.getEmail()), "Debe retornar true para email existente");

        // Email no existe
        assertFalse(usuarioService.existeEmail("noexiste@email.com"), "Debe retornar false para email no existente");

        // Email null
        assertFalse(usuarioService.existeEmail(null), "Debe retornar false cuando email es null");
    }

    @Test
    void testFindById() {
        // ID existe
        Long id = usuarioExistente.getIdUsuario();
        Optional<Usuario> resultado = usuarioService.findById(id);
        assertTrue(resultado.isPresent(), "Debe encontrar el usuario");
        assertEquals(id, resultado.get().getIdUsuario());

        // ID no existe
        Long idNoExistente = 99999L;
        resultado = usuarioService.findById(idNoExistente);
        assertFalse(resultado.isPresent(), "No debe encontrar el usuario");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.findById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.findById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.findById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");
    }

    @Test
    void testFindAll() {
        List<Usuario> usuarios = usuarioService.findAll();
        assertNotNull(usuarios, "La lista no debe ser null");
        assertFalse(usuarios.isEmpty(), "La lista no debe estar vacía");
        assertTrue(usuarios.size() >= 4, "Debe haber al menos 4 usuarios de data.sql");
    }

    @Test
    void testDeleteById() {
        // Caso exitoso
        Long id = usuarioExistente.getIdUsuario();
        usuarioService.deleteById(id);
        Optional<Usuario> resultado = usuarioService.findById(id);
        assertFalse(resultado.isPresent(), "El usuario debe haber sido eliminado");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.deleteById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.deleteById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");
    }

    @Test
    void testActualizarPerfil() {
        // Caso exitoso
        usuarioExistente.setNombre("NuevoNombre");
        usuarioExistente.setApellido1("NuevoApellido");
        Usuario resultado = usuarioService.actualizarPerfil(usuarioExistente);
        assertNotNull(resultado);
        assertEquals("NuevoNombre", resultado.getNombre());
        assertEquals("NuevoApellido", resultado.getApellido1());

        // Usuario no existe en la BBDD
        Usuario usuarioNoExistente = new Usuario("Test", "Test", null, "test@email.com", "testuser", "password123");
        usuarioNoExistente.setIdUsuario(99999L);
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarPerfil(usuarioNoExistente);
        }, "Debe lanzar excepción cuando el usuario no existe");

        // Nombre vacío - crear nuevo usuario limpio para evitar problemas con la sesión
        Usuario usuarioParaNombreVacio = usuarioService.findByUsername("juanp").orElseThrow();
        usuarioParaNombreVacio.setNombre("");
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarPerfil(usuarioParaNombreVacio);
        }, "Debe lanzar excepción cuando el nombre está vacío");

        // Username ya en uso - necesitamos hacerlo de manera que no cause autoflush
        // Obtenemos usuario maria de nuevo para tener estado limpio
        Usuario usuarioMaria = usuarioService.findByUsername("maria").orElseThrow();
        Long idMaria = usuarioMaria.getIdUsuario();

        // Creamos un nuevo objeto Usuario con los datos de maria pero username de juanp
        Usuario usuarioConUsernameDuplicado = new Usuario(
                usuarioMaria.getNombre(),
                usuarioMaria.getApellido1(),
                usuarioMaria.getApellido2(),
                usuarioMaria.getEmail(),
                "juanp", // username que ya existe
                usuarioMaria.getPassword()
        );
        usuarioConUsernameDuplicado.setIdUsuario(idMaria);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarPerfil(usuarioConUsernameDuplicado);
        }, "Debe lanzar excepción cuando el username ya está en uso por otro usuario");

        // Email ya en uso
        Usuario usuarioAlicia4 = usuarioService.findByUsername("alicia4").orElseThrow();
        Usuario usuarioJuan = usuarioService.findByUsername("juanp").orElseThrow();
        Long idAlicia4 = usuarioAlicia4.getIdUsuario();
        String emailJuan = usuarioJuan.getEmail();

        Usuario usuarioConEmailDuplicado = new Usuario(
                usuarioAlicia4.getNombre(),
                usuarioAlicia4.getApellido1(),
                usuarioAlicia4.getApellido2(),
                emailJuan, // email que ya existe
                usuarioAlicia4.getUsername(),
                usuarioAlicia4.getPassword()
        );
        usuarioConEmailDuplicado.setIdUsuario(idAlicia4);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarPerfil(usuarioConEmailDuplicado);
        }, "Debe lanzar excepción cuando el email ya está en uso por otro usuario");

        // Email formato inválido
        Usuario usuarioParaEmailInvalido = usuarioService.findByUsername("alicia4").orElseThrow();
        Long idUsuarioEmail = usuarioParaEmailInvalido.getIdUsuario();

        Usuario usuarioEmailInvalido = new Usuario(
                usuarioParaEmailInvalido.getNombre(),
                usuarioParaEmailInvalido.getApellido1(),
                usuarioParaEmailInvalido.getApellido2(),
                "emailinvalido", // email sin formato válido
                usuarioParaEmailInvalido.getUsername(),
                usuarioParaEmailInvalido.getPassword()
        );
        usuarioEmailInvalido.setIdUsuario(idUsuarioEmail);

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.actualizarPerfil(usuarioEmailInvalido);
        }, "Debe lanzar excepción cuando el formato del email es inválido");
    }

    @Test
    void testCambiarPassword() {
        // Caso exitoso
        Usuario usuario = usuarioService.findByUsername("maria").orElseThrow();
        Long id = usuario.getIdUsuario();
        String passwordActual = usuario.getPassword();
        String nuevaPassword = "nuevaPassword123";
        Usuario resultado = usuarioService.cambiarPassword(id, passwordActual, nuevaPassword);
        assertNotNull(resultado);
        assertEquals(nuevaPassword, resultado.getPassword());

        // Usuario no existe
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarPassword(99999L, "password123", "nuevaPassword123");
        }, "Debe lanzar excepción cuando el usuario no existe");

        // Password actual incorrecta
        Usuario usuario2 = usuarioService.findByUsername("juanp").orElseThrow();
        Long id2 = usuario2.getIdUsuario();
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarPassword(id2, "passwordIncorrecta", "nuevaPassword123");
        }, "Debe lanzar excepción cuando la contraseña actual es incorrecta");

        // Nueva password null
        Usuario usuario3 = usuarioService.findByUsername("alicia4").orElseThrow();
        Long id3 = usuario3.getIdUsuario();
        String password3 = usuario3.getPassword();
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarPassword(id3, password3, null);
        }, "Debe lanzar excepción cuando la nueva contraseña es null");

        // Nueva password corta
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarPassword(id3, password3, "corta");
        }, "Debe lanzar excepción cuando la nueva contraseña es muy corta");
    }

    @Test
    void testFindByUsername() {
        // Username existe
        Optional<Usuario> resultado = usuarioService.findByUsername("alicia");
        assertTrue(resultado.isPresent(), "Debe encontrar el usuario");
        assertEquals("alicia", resultado.get().getUsername());

        // Username no existe
        resultado = usuarioService.findByUsername("usuarioNoExistente");
        assertFalse(resultado.isPresent(), "No debe encontrar el usuario");

        // Username null
        resultado = usuarioService.findByUsername(null);
        assertFalse(resultado.isPresent(), "Debe retornar Optional vacío cuando username es null");
    }

    @Test
    void testFindByEmail() {
        // Email existe
        Optional<Usuario> resultado = usuarioService.findByEmail(usuarioExistente.getEmail());
        assertTrue(resultado.isPresent(), "Debe encontrar el usuario");
        assertEquals(usuarioExistente.getEmail(), resultado.get().getEmail());

        // Email no existe
        resultado = usuarioService.findByEmail("noexiste@email.com");
        assertFalse(resultado.isPresent(), "No debe encontrar el usuario");

        // Email null
        resultado = usuarioService.findByEmail(null);
        assertFalse(resultado.isPresent(), "Debe retornar Optional vacío cuando email es null");
    }

    @Test
    void testBuscarPorNombre() {
        // Nombre existe
        List<Usuario> resultados = usuarioService.buscarPorNombre("ali");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(u -> u.getNombre().toLowerCase().contains("ali")));

        // Nombre no existe
        resultados = usuarioService.buscarPorNombre("NombreQueNoExiste");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Nombre null
        resultados = usuarioService.buscarPorNombre(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Nombre vacío
        resultados = usuarioService.buscarPorNombre("");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testBuscarPorUsername() {
        // Username existe
        List<Usuario> resultados = usuarioService.buscarPorUsername("ali");
        assertNotNull(resultados);
        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(u -> u.getUsername().toLowerCase().contains("ali")));

        // Username no existe
        resultados = usuarioService.buscarPorUsername("usernameNoExistente");
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());

        // Username null
        resultados = usuarioService.buscarPorUsername(null);
        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testValidarCredenciales() {
        // Credenciales correctas
        assertTrue(usuarioService.validarCredenciales("alicia", "aliciaPass!23"), "Debe retornar true para credenciales correctas");

        // Credenciales incorrectas
        assertFalse(usuarioService.validarCredenciales("alicia", "passwordIncorrecta"), "Debe retornar false para credenciales incorrectas");

        // Username null
        assertFalse(usuarioService.validarCredenciales(null, "password123"), "Debe retornar false cuando username es null");

        // Username vacío
        assertFalse(usuarioService.validarCredenciales("", "password123"), "Debe retornar false cuando username está vacío");

        // Password null
        assertFalse(usuarioService.validarCredenciales("alicia", null), "Debe retornar false cuando password es null");

        // Password vacío
        assertFalse(usuarioService.validarCredenciales("alicia", ""), "Debe retornar false cuando password está vacío");
    }
}
