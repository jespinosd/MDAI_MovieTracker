package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.services.ColeccionService;
import es.unex.cum.mdai.movieTracker.data.services.PeliculaService;
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
public class ColeccionServiceTest {

    @Autowired
    private ColeccionService coleccionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PeliculaService peliculaService;

    private Coleccion coleccionExistente;
    private Usuario usuarioAlicia;
    private Usuario usuarioJuan;
    private Pelicula peliculaPadrino;
    private Pelicula peliculaInception;

    @BeforeEach
    void setUp() {
        // Recuperar usuarios de prueba
        usuarioAlicia = usuarioService.findByUsername("alicia").orElse(null);
        assertNotNull(usuarioAlicia, "El usuario Alicia debe existir");

        usuarioJuan = usuarioService.findByUsername("juanp").orElse(null);
        assertNotNull(usuarioJuan, "El usuario Juan debe existir");

        // Recuperar películas de prueba
        peliculaPadrino = peliculaService.findByTitulo("El Padrino").orElse(null);
        assertNotNull(peliculaPadrino, "La película El Padrino debe existir");

        peliculaInception = peliculaService.findByTitulo("Inception").orElse(null);
        assertNotNull(peliculaInception, "La película Inception debe existir");

        // Recuperar colección existente (Alicia tiene una colección con películas)
        coleccionExistente = coleccionService.findByUsuario(usuarioAlicia.getIdUsuario()).orElse(null);
        assertNotNull(coleccionExistente, "La colección de prueba debe existir");
    }

    @Test
    void testFindById() {
        // ID existe
        Long id = coleccionExistente.getIdColeccion();
        Optional<Coleccion> resultado = coleccionService.findById(id);
        assertTrue(resultado.isPresent(), "Debe encontrar la colección");
        assertEquals(id, resultado.get().getIdColeccion());

        // ID no existe
        Long idNoExistente = 99999L;
        resultado = coleccionService.findById(idNoExistente);
        assertFalse(resultado.isPresent(), "No debe encontrar la colección");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.findById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.findById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.findById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");
    }

    @Test
    void testFindAll() {
        List<Coleccion> colecciones = coleccionService.findAll();
        assertNotNull(colecciones, "La lista no debe ser null");
        assertFalse(colecciones.isEmpty(), "La lista no debe estar vacía");
        assertTrue(colecciones.size() >= 4, "Debe haber al menos 4 colecciones de data.sql");
    }

    @Test
    void testSave() {
        // Caso exitoso - actualizar colección existente
        int tamanioInicial = coleccionExistente.getListaPeliculas().size();
        Coleccion resultado = coleccionService.save(coleccionExistente);
        assertNotNull(resultado, "La colección debe ser guardada");
        assertEquals(coleccionExistente.getIdColeccion(), resultado.getIdColeccion());

        // Colección null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.save(null);
        }, "Debe lanzar excepción cuando la colección es null");
    }

    @Test
    void testDeleteById() {
        // Caso exitoso
        Long id = coleccionExistente.getIdColeccion();
        coleccionService.deleteById(id);
        Optional<Coleccion> resultado = coleccionService.findById(id);
        assertFalse(resultado.isPresent(), "La colección debe haber sido eliminada");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.deleteById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.deleteById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.deleteById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");
    }

    @Test
    void testFindByUsuario() {
        // Usuario con colección (Alicia tiene colección)
        Optional<Coleccion> resultado = coleccionService.findByUsuario(usuarioAlicia.getIdUsuario());
        assertTrue(resultado.isPresent(), "Debe encontrar la colección del usuario");
        assertEquals(usuarioAlicia.getIdUsuario(), resultado.get().getUsuario().getIdUsuario());

        // ID usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.findByUsuario(null);
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.findByUsuario(-1L);
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID usuario cero
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.findByUsuario(0L);
        }, "Debe lanzar excepción cuando el ID del usuario es cero");

        // Usuario que no existe
        Optional<Coleccion> coleccionInexistente = coleccionService.findByUsuario(99999L);
        assertFalse(coleccionInexistente.isPresent(), "No debe encontrar colección para usuario inexistente");
    }

    @Test
    void testCrearColeccionParaUsuario() {
        // Al registrar un usuario, automáticamente se crea su colección
        // Por lo tanto, todos los usuarios registrados YA tienen colección
        final Usuario nuevoUsuario = usuarioService.registrar(new Usuario(
                "Test",
                "Usuario",
                null,
                "test@test.com",
                "testusuario",
                "password123"
        ));

        // Verificar que el usuario YA tiene colección (creada automáticamente al registrarse)
        assertTrue(coleccionService.usuarioTieneColeccion(nuevoUsuario.getIdUsuario()),
                "El usuario debe tener colección automáticamente al registrarse");

        // Intentar crear colección para usuario que ya tiene una (debería lanzar excepción)
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.crearColeccionParaUsuario(nuevoUsuario.getIdUsuario());
        }, "Debe lanzar excepción cuando el usuario ya tiene colección");

        // También debe lanzar excepción para Alicia que ya tiene colección
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.crearColeccionParaUsuario(usuarioAlicia.getIdUsuario());
        }, "Debe lanzar excepción cuando el usuario ya tiene colección");

        // ID usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.crearColeccionParaUsuario(null);
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.crearColeccionParaUsuario(-1L);
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID usuario cero
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.crearColeccionParaUsuario(0L);
        }, "Debe lanzar excepción cuando el ID del usuario es cero");

        // Usuario que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.crearColeccionParaUsuario(99999L);
        }, "Debe lanzar excepción cuando el usuario no existe");
    }

    @Test
    void testUsuarioTieneColeccion() {
        // Usuario con colección (Alicia tiene colección)
        assertTrue(coleccionService.usuarioTieneColeccion(usuarioAlicia.getIdUsuario()),
                "Debe retornar true cuando el usuario tiene colección");

        // Usuario recién registrado también tiene colección (se crea automáticamente)
        Usuario nuevoUsuario = new Usuario(
                "Test2",
                "Usuario2",
                null,
                "test2@test.com",
                "testusuario2",
                "password123"
        );
        nuevoUsuario = usuarioService.registrar(nuevoUsuario);

        assertTrue(coleccionService.usuarioTieneColeccion(nuevoUsuario.getIdUsuario()),
                "Debe retornar true cuando el usuario registrado tiene colección automáticamente");

        // ID usuario null
        assertFalse(coleccionService.usuarioTieneColeccion(null),
                "Debe retornar false cuando el ID del usuario es null");

        // ID usuario negativo
        assertFalse(coleccionService.usuarioTieneColeccion(-1L),
                "Debe retornar false cuando el ID del usuario es negativo");

        // ID usuario cero
        assertFalse(coleccionService.usuarioTieneColeccion(0L),
                "Debe retornar false cuando el ID del usuario es cero");

        // Usuario que no existe
        assertFalse(coleccionService.usuarioTieneColeccion(99999L),
                "Debe retornar false cuando el usuario no existe");
    }

    @Test
    void testAgregarPeliculaAColeccion() {
        // Agregar película que no está en la colección
        Pelicula peliculaInterstellar = peliculaService.findByTitulo("Interstellar").orElseThrow();
        int tamanioInicial = coleccionService.obtenerPeliculasDeColeccion(usuarioAlicia.getIdUsuario()).size();

        Coleccion resultado = coleccionService.agregarPeliculaAColeccion(
                usuarioAlicia.getIdUsuario(),
                peliculaInterstellar.getIdPelicula()
        );

        assertNotNull(resultado);
        assertEquals(tamanioInicial + 1, resultado.getListaPeliculas().size());
        assertTrue(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), peliculaInterstellar.getIdPelicula()));

        // Intentar agregar película que ya está en la colección
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(usuarioAlicia.getIdUsuario(), peliculaPadrino.getIdPelicula());
        }, "Debe lanzar excepción cuando la película ya está en la colección");

        // ID usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(null, peliculaInception.getIdPelicula());
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(-1L, peliculaInception.getIdPelicula());
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID película null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(usuarioAlicia.getIdUsuario(), null);
        }, "Debe lanzar excepción cuando el ID de la película es null");

        // ID película negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(usuarioAlicia.getIdUsuario(), -1L);
        }, "Debe lanzar excepción cuando el ID de la película es negativo");

        // Usuario que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(99999L, peliculaInception.getIdPelicula());
        }, "Debe lanzar excepción cuando el usuario no existe");

        // Película que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.agregarPeliculaAColeccion(usuarioAlicia.getIdUsuario(), 99999L);
        }, "Debe lanzar excepción cuando la película no existe");
    }

    @Test
    void testEliminarPeliculaDeColeccion() {
        // Verificar que El Padrino está en la colección de Alicia
        assertTrue(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), peliculaPadrino.getIdPelicula()));

        int tamanioInicial = coleccionService.obtenerPeliculasDeColeccion(usuarioAlicia.getIdUsuario()).size();

        // Eliminar película que está en la colección
        Coleccion resultado = coleccionService.eliminarPeliculaDeColeccion(
                usuarioAlicia.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        );

        assertNotNull(resultado);
        assertEquals(tamanioInicial - 1, resultado.getListaPeliculas().size());
        assertFalse(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), peliculaPadrino.getIdPelicula()));

        // Intentar eliminar película que no está en la colección
        Pelicula peliculaInterstellar = peliculaService.findByTitulo("Interstellar").orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(usuarioAlicia.getIdUsuario(), peliculaInterstellar.getIdPelicula());
        }, "Debe lanzar excepción cuando la película no está en la colección");

        // ID usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(null, peliculaInception.getIdPelicula());
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(-1L, peliculaInception.getIdPelicula());
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID película null
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(usuarioAlicia.getIdUsuario(), null);
        }, "Debe lanzar excepción cuando el ID de la película es null");

        // ID película negativo
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(usuarioAlicia.getIdUsuario(), -1L);
        }, "Debe lanzar excepción cuando el ID de la película es negativo");

        // Usuario que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(99999L, peliculaInception.getIdPelicula());
        }, "Debe lanzar excepción cuando el usuario no existe");

        // Película que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.eliminarPeliculaDeColeccion(usuarioAlicia.getIdUsuario(), 99999L);
        }, "Debe lanzar excepción cuando la película no existe");
    }

    @Test
    void testPeliculaEstaEnColeccion() {
        // Película está en la colección (Alicia tiene El Padrino)
        assertTrue(coleccionService.peliculaEstaEnColeccion(
                usuarioAlicia.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        ), "Debe retornar true cuando la película está en la colección");

        // Película no está en la colección
        Pelicula peliculaInterstellar = peliculaService.findByTitulo("Interstellar").orElseThrow();
        assertFalse(coleccionService.peliculaEstaEnColeccion(
                usuarioAlicia.getIdUsuario(),
                peliculaInterstellar.getIdPelicula()
        ), "Debe retornar false cuando la película no está en la colección");

        // ID usuario null
        assertFalse(coleccionService.peliculaEstaEnColeccion(null, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el ID del usuario es null");

        // ID usuario negativo
        assertFalse(coleccionService.peliculaEstaEnColeccion(-1L, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el ID del usuario es negativo");

        // ID usuario cero
        assertFalse(coleccionService.peliculaEstaEnColeccion(0L, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el ID del usuario es cero");

        // ID película null
        assertFalse(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), null),
                "Debe retornar false cuando el ID de la película es null");

        // ID película negativo
        assertFalse(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), -1L),
                "Debe retornar false cuando el ID de la película es negativo");

        // ID película cero
        assertFalse(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), 0L),
                "Debe retornar false cuando el ID de la película es cero");

        // Usuario que no existe
        assertFalse(coleccionService.peliculaEstaEnColeccion(99999L, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el usuario no existe");

        // Película que no existe
        assertFalse(coleccionService.peliculaEstaEnColeccion(usuarioAlicia.getIdUsuario(), 99999L),
                "Debe retornar false cuando la película no existe");
    }

    @Test
    void testObtenerPeliculasDeColeccion() {
        // Usuario con películas en su colección (Alicia tiene El Padrino e Inception)
        List<Pelicula> peliculas = coleccionService.obtenerPeliculasDeColeccion(usuarioAlicia.getIdUsuario());
        assertNotNull(peliculas);
        assertFalse(peliculas.isEmpty());
        assertEquals(2, peliculas.size());
        assertTrue(peliculas.stream().anyMatch(p -> p.getTitulo().equals("El Padrino")));
        assertTrue(peliculas.stream().anyMatch(p -> p.getTitulo().equals("Inception")));

        // Usuario con una película (Juan tiene colección pero con una película)
        List<Pelicula> peliculasJuan = coleccionService.obtenerPeliculasDeColeccion(usuarioJuan.getIdUsuario());
        assertNotNull(peliculasJuan);
        assertFalse(peliculasJuan.isEmpty());

        // Usuario inexistente o con colección vacía - debe devolver lista vacía en lugar de excepción
        List<Pelicula> peliculasUsuarioInexistente = coleccionService.obtenerPeliculasDeColeccion(99999L);
        assertNotNull(peliculasUsuarioInexistente, "Debe devolver una lista (vacía) incluso si el usuario no existe");
        assertTrue(peliculasUsuarioInexistente.isEmpty(), "La lista debe estar vacía para usuario inexistente");

        // ID usuario null - debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.obtenerPeliculasDeColeccion(null);
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo - debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.obtenerPeliculasDeColeccion(-1L);
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID usuario cero - debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> {
            coleccionService.obtenerPeliculasDeColeccion(0L);
        }, "Debe lanzar excepción cuando el ID del usuario es cero");
    }
}
