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
        // Crear el usuario
        Usuario nuevo = new Usuario("Pedro", "Gomez", "Lopez", "pedro.gomez@test.com", "pedro", "clave");

        // Crear la colección y asociarla
        Coleccion coleccion = new Coleccion();
        coleccion.setUsuario(nuevo);
        nuevo.setColeccion(coleccion);

        // Crear la película
        Pelicula pelicula = new Pelicula("Película Test", 2020, "Director Test", "Drama", "Sinopsis de prueba", null);
        peliculaRepository.save(pelicula);

        // Crear valoraciones y asociarlas al usuario y a la película
        Valoracion v1 = new Valoracion();
        v1.setUsuario(nuevo);
        v1.setPelicula(pelicula);
        v1.setPuntuacion(5);
        v1.setComentario("Excelente película");

        Valoracion v2 = new Valoracion();
        v2.setUsuario(nuevo);
        v2.setPelicula(pelicula);
        v2.setPuntuacion(4);
        v2.setComentario("Muy buena");

        nuevo.setListaValoraciones(List.of(v1, v2));

        // Guardar solo el usuario (gracias al cascade se guarda todo)
        Usuario guardado = usuarioRepository.save(nuevo);

        Long idUsuario = guardado.getIdUsuario();
        Long idColeccion = guardado.getColeccion().getIdColeccion();

        // Guardar los ids de las valoraciones
        Long idValoracion1 = guardado.getListaValoraciones().get(0).getIdValoracion();
        Long idValoracion2 = guardado.getListaValoraciones().get(1).getIdValoracion();

        // Comprobaciones previas
        assertNotNull(idUsuario);
        assertTrue(coleccionRepository.findById(idColeccion).isPresent());
        assertTrue(valoracionRepository.findById(idValoracion1).isPresent());
        assertTrue(valoracionRepository.findById(idValoracion2).isPresent());

        // Eliminar usuario
        usuarioRepository.delete(guardado);

        // Comprobaciones posteriores
        assertNull(usuarioRepository.findByIdUsuario(idUsuario));
        assertFalse(coleccionRepository.findById(idColeccion).isPresent());
        assertTrue(valoracionRepository.findByUsuario_IdUsuario(idUsuario).isEmpty());
    }


    @Test
    void testCount() {
        long count = usuarioRepository.count();
        assertEquals(4, count);
    }
}
