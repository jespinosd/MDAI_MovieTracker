package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.repository.ColeccionRepository;
import es.unex.cum.mdai.movieTracker.data.repository.PeliculaRepository;
import es.unex.cum.mdai.movieTracker.data.repository.UsuarioRepository;
import es.unex.cum.mdai.movieTracker.data.repository.ValoracionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ColeccionRepository coleccionRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Usuario usuario3;
    private Usuario usuario4;

    @BeforeEach
    void setUp() {
        // Limpiar antes
        usuarioRepository.deleteAll();
        coleccionRepository.deleteAll();
        valoracionRepository.deleteAll();
        peliculaRepository.deleteAll();

        // Crear usuarios de prueba
        usuario1 = new Usuario("Alicia", "Sánchez", "Fernández", "alicia.sanchez@test.com", "alicia", "aliciaPass!23");
        usuario2 = new Usuario("Juan", "Pérez", "García", "juan.perez@test.com", "juanp", "secret");
        usuario3 = new Usuario("María", "López", "Ruiz", "maria.lopez@test.com", "maria", "pwd");
        usuario4 = new Usuario("Alicia", "Gómez", "Ruiz", "alicia4.gomez@test.com", "alicia4", "alicia4Pass");

        usuarioRepository.save(usuario1);
        usuarioRepository.save(usuario2);
        usuarioRepository.save(usuario3);
        usuarioRepository.save(usuario4);
    }

    @Test
    void testFindByIdUsuario() {
        Usuario encontrado = usuarioRepository.findByIdUsuario(usuario1.getIdUsuario());
        assertNotNull(encontrado);
        assertEquals("Alicia", encontrado.getNombre());

        Usuario noEncontrado = usuarioRepository.findByIdUsuario(999L);
        assertNull(noEncontrado);
    }

    @Test
    void testFindByNombre() {
        Usuario encontrado = usuarioRepository.findByNombre("Juan");
        assertNotNull(encontrado);
        assertEquals("juanp", encontrado.getUsername());

        Usuario noEncontrado = usuarioRepository.findByNombre("NombreInexistente");
        assertNull(noEncontrado);
    }

    @Test
    void testFindByUsername() {
        Usuario encontrado = usuarioRepository.findByUsername("alicia");
        assertNotNull(encontrado);
        assertEquals("alicia.sanchez@test.com", encontrado.getEmail());

        Usuario noEncontrado = usuarioRepository.findByUsername("noexiste");
        assertNull(noEncontrado);
    }

    @Test
    void testFindByEmail() {
        Usuario encontrado = usuarioRepository.findByEmail("maria.lopez@test.com");
        assertNotNull(encontrado);
        assertEquals("María", encontrado.getNombre());

        Usuario noEncontrado = usuarioRepository.findByEmail("no@mail.com");
        assertNull(noEncontrado);
    }

    @Test
    void testFindByUsernameAndPassword() {
        Usuario correcto = usuarioRepository.findByUsernameAndPassword("juanp", "secret");
        assertNotNull(correcto);
        assertEquals("Juan", correcto.getNombre());

        Usuario incorrecto = usuarioRepository.findByUsernameAndPassword("juanp", "wrong");
        assertNull(incorrecto);
    }

    @Test
    void testFindByNombreContainingIgnoreCase() {
        List<Usuario> resultados = usuarioRepository.findByNombreContainingIgnoreCase("ali");
        assertNotNull(resultados);
        // Debería encontrar ambas entradas con nombre 'Alicia'
        assertEquals(2, resultados.size());
        assertEquals("alicia", resultados.get(0).getUsername());
        assertEquals("alicia4", resultados.get(1).getUsername());

        List<Usuario> resultadosMayusculas = usuarioRepository.findByNombreContainingIgnoreCase("ALICIA");
        assertNotNull(resultadosMayusculas);
        assertEquals(2, resultadosMayusculas.size());
        assertEquals("alicia", resultados.get(0).getUsername());
        assertEquals("alicia4", resultados.get(1).getUsername());
    }

    @Test
    void testFindByUsernameContainingIgnoreCase() {
        List<Usuario> resultados = usuarioRepository.findByUsernameContainingIgnoreCase("ali");
        assertNotNull(resultados);
        // Debería devolver tanto 'alicia' como 'alicia4'
        assertEquals(2, resultados.size());
        assertEquals("alicia", resultados.get(0).getUsername());
        assertEquals("alicia4", resultados.get(1).getUsername());
    }

    // CRUD Tests
    @Test
    void testGuardarUsuario() {
        Usuario nuevo = new Usuario("Pedro", "Gomez", "Lopez", "pedro.gomez@test.com", "pedro", "clave");
        Usuario guardado = usuarioRepository.save(nuevo);

        assertNotNull(guardado.getIdUsuario());
        Usuario recuperado = usuarioRepository.findByIdUsuario(guardado.getIdUsuario());
        assertNotNull(recuperado);
        assertEquals("Pedro", recuperado.getNombre());
    }

    @Test
    void testActualizarUsuario() {
        Usuario nuevo = new Usuario("Pedro", "Gomez", "Lopez", "pedro.gomez@test.com", "pedro", "clave");
        Usuario guardado = usuarioRepository.save(nuevo);

        guardado.setPassword("nuevaclave");
        usuarioRepository.save(guardado);

        Usuario actualizado = usuarioRepository.findByUsernameAndPassword("pedro", "nuevaclave");
        assertNotNull(actualizado);
        assertEquals("pedro.gomez@test.com", actualizado.getEmail());
    }

    @Test
    void testEliminarUsuario() {
        // Crear un usuario con colección y valoraciones
        Usuario nuevo = new Usuario("Pedro", "Gomez", "Lopez", "pedro.gomez@test.com", "pedro", "clave");
        Usuario guardado = usuarioRepository.save(nuevo);

        // Crear una película para poder valorar
        Pelicula pelicula = new Pelicula("Película Test", 2020, "Director Test", "Drama", "Sinopsis de prueba", null);
        peliculaRepository.save(pelicula);

        // Crear y asociar una colección al usuario
        Coleccion coleccion = new Coleccion(guardado);
        coleccionRepository.save(coleccion);
        guardado.setColeccion(coleccion);

        // Crear valoraciones y asociarlas al usuario y a la película
        Valoracion valoracion1 = new Valoracion(guardado, pelicula, 5, "Excelente película");
        Valoracion valoracion2 = new Valoracion(guardado, pelicula, 4, "Muy buena");
        valoracionRepository.save(valoracion1);
        valoracionRepository.save(valoracion2);

        // Guardar los ids antes de eliminar
        Long idUsuario = guardado.getIdUsuario();
        Long idColeccion = coleccion.getIdColeccion();
        Long idValoracion1 = valoracion1.getIdValoracion();
        Long idValoracion2 = valoracion2.getIdValoracion();

        // Verificar que existen antes de eliminar
        assertNotNull(usuarioRepository.findByIdUsuario(idUsuario));
        assertTrue(coleccionRepository.findById(idColeccion).isPresent());
        assertTrue(valoracionRepository.findById(idValoracion1).isPresent());
        assertTrue(valoracionRepository.findById(idValoracion2).isPresent());

        // Eliminar el usuario
        usuarioRepository.delete(guardado);

        // Verificar que el usuario ha sido eliminado
        Usuario trasEliminacion = usuarioRepository.findByIdUsuario(idUsuario);
        assertNull(trasEliminacion);

        // Verificar que la colección asociada ha sido eliminada (cascade)
        assertFalse(coleccionRepository.findById(idColeccion).isPresent());

        // Verificar que las valoraciones del usuario han sido eliminadas (cascade)
        List<Valoracion> valoracionesTrasEliminacion = valoracionRepository.findByUsuario_IdUsuario(idUsuario);
        assertTrue(valoracionesTrasEliminacion.isEmpty());
        assertFalse(valoracionRepository.findById(idValoracion1).isPresent());
        assertFalse(valoracionRepository.findById(idValoracion2).isPresent());
    }

    @Test
    void testCount() {
        long count = usuarioRepository.count();
        assertEquals(4, count);
    }
}

