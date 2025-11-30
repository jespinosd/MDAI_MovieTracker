package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.services.PeliculaService;
import es.unex.cum.mdai.movieTracker.data.services.UsuarioService;
import es.unex.cum.mdai.movieTracker.data.services.ValoracionService;
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
public class ValoracionServiceTest {

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PeliculaService peliculaService;

    private Valoracion valoracionExistente;
    private Usuario usuarioAlicia;
    private Pelicula peliculaPadrino;

    @BeforeEach
    void setUp() {
        // Recuperar usuario y película de prueba
        usuarioAlicia = usuarioService.findByUsername("alicia").orElse(null);
        assertNotNull(usuarioAlicia, "El usuario de prueba debe existir");

        peliculaPadrino = peliculaService.findByTitulo("El Padrino").orElse(null);
        assertNotNull(peliculaPadrino, "La película de prueba debe existir");

        // Recuperar una valoración existente (Alicia valoró El Padrino con 7)
        valoracionExistente = valoracionService.findByUsuarioAndPelicula(
                usuarioAlicia.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        ).orElse(null);
        assertNotNull(valoracionExistente, "La valoración de prueba debe existir");
    }

    @Test
    void testFindById() {
        // ID existe
        Long id = valoracionExistente.getIdValoracion();
        Optional<Valoracion> resultado = valoracionService.findById(id);
        assertTrue(resultado.isPresent(), "Debe encontrar la valoración");
        assertEquals(id, resultado.get().getIdValoracion());

        // ID no existe
        Long idNoExistente = 99999L;
        resultado = valoracionService.findById(idNoExistente);
        assertFalse(resultado.isPresent(), "No debe encontrar la valoración");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");
    }

    @Test
    void testFindAll() {
        List<Valoracion> valoraciones = valoracionService.findAll();
        assertNotNull(valoraciones, "La lista no debe ser null");
        assertFalse(valoraciones.isEmpty(), "La lista no debe estar vacía");
        assertTrue(valoraciones.size() >= 3, "Debe haber al menos 3 valoraciones de data.sql");
    }

    @Test
    void testSave() {
        // Caso exitoso - nueva valoración
        Usuario usuarioMaria = usuarioService.findByUsername("maria").orElseThrow();
        Pelicula peliculaInception = peliculaService.findByTitulo("Inception").orElseThrow();

        Valoracion nuevaValoracion = new Valoracion();
        nuevaValoracion.setUsuario(usuarioMaria);
        nuevaValoracion.setPelicula(peliculaInception);
        nuevaValoracion.setPuntuacion(8);
        nuevaValoracion.setComentario("Muy buena película");

        Valoracion resultado = valoracionService.save(nuevaValoracion);
        assertNotNull(resultado, "La valoración debe ser guardada");
        assertNotNull(resultado.getIdValoracion(), "La valoración debe tener un ID asignado");
        assertEquals(8, resultado.getPuntuacion());
        assertEquals("Muy buena película", resultado.getComentario());

        // Valoración null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.save(null);
        }, "Debe lanzar excepción cuando la valoración es null");

        // Puntuación menor a 1
        Valoracion valoracionPuntuacionBaja = new Valoracion();
        valoracionPuntuacionBaja.setUsuario(usuarioAlicia);
        valoracionPuntuacionBaja.setPelicula(peliculaPadrino);
        valoracionPuntuacionBaja.setPuntuacion(0);
        valoracionPuntuacionBaja.setComentario("Test");
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.save(valoracionPuntuacionBaja);
        }, "Debe lanzar excepción cuando la puntuación es menor a 1");

        // Puntuación mayor a 10
        Valoracion valoracionPuntuacionAlta = new Valoracion();
        valoracionPuntuacionAlta.setUsuario(usuarioAlicia);
        valoracionPuntuacionAlta.setPelicula(peliculaPadrino);
        valoracionPuntuacionAlta.setPuntuacion(11);
        valoracionPuntuacionAlta.setComentario("Test");
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.save(valoracionPuntuacionAlta);
        }, "Debe lanzar excepción cuando la puntuación es mayor a 10");

        // Usuario null
        Valoracion valoracionUsuarioNull = new Valoracion();
        valoracionUsuarioNull.setUsuario(null);
        valoracionUsuarioNull.setPelicula(peliculaPadrino);
        valoracionUsuarioNull.setPuntuacion(5);
        valoracionUsuarioNull.setComentario("Test");
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.save(valoracionUsuarioNull);
        }, "Debe lanzar excepción cuando el usuario es null");

        // Película null
        Valoracion valoracionPeliculaNull = new Valoracion();
        valoracionPeliculaNull.setUsuario(usuarioAlicia);
        valoracionPeliculaNull.setPelicula(null);
        valoracionPeliculaNull.setPuntuacion(5);
        valoracionPeliculaNull.setComentario("Test");
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.save(valoracionPeliculaNull);
        }, "Debe lanzar excepción cuando la película es null");
    }

    @Test
    void testDeleteById() {
        // Caso exitoso
        Long id = valoracionExistente.getIdValoracion();
        valoracionService.deleteById(id);
        Optional<Valoracion> resultado = valoracionService.findById(id);
        assertFalse(resultado.isPresent(), "La valoración debe haber sido eliminada");

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.deleteById(null);
        }, "Debe lanzar excepción cuando el ID es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.deleteById(-1L);
        }, "Debe lanzar excepción cuando el ID es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.deleteById(0L);
        }, "Debe lanzar excepción cuando el ID es cero");

        // ID que no existe
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.deleteById(99999L);
        }, "Debe lanzar excepción cuando el ID no existe");
    }

    @Test
    void testFindByUsuario() {
        // Usuario con valoraciones (Alicia tiene 2: El Padrino e Inception)
        List<Valoracion> valoraciones = valoracionService.findByUsuario(usuarioAlicia.getIdUsuario());
        assertNotNull(valoraciones);
        assertFalse(valoraciones.isEmpty());
        assertEquals(2, valoraciones.size());
        assertTrue(valoraciones.stream().allMatch(v -> v.getUsuario().getIdUsuario().equals(usuarioAlicia.getIdUsuario())));

        // Usuario sin valoraciones
        Usuario usuarioMaria = usuarioService.findByUsername("maria").orElseThrow();
        valoraciones = valoracionService.findByUsuario(usuarioMaria.getIdUsuario());
        assertNotNull(valoraciones);
        assertTrue(valoraciones.isEmpty());

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuario(null);
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuario(-1L);
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuario(0L);
        }, "Debe lanzar excepción cuando el ID del usuario es cero");
    }

    @Test
    void testFindByPelicula() {
        // Película con valoraciones (El Padrino tiene 2: Alicia y Juan)
        List<Valoracion> valoraciones = valoracionService.findByPelicula(peliculaPadrino.getIdPelicula());
        assertNotNull(valoraciones);
        assertFalse(valoraciones.isEmpty());
        assertEquals(2, valoraciones.size());
        assertTrue(valoraciones.stream().allMatch(v -> v.getPelicula().getIdPelicula().equals(peliculaPadrino.getIdPelicula())));

        // Película sin valoraciones
        Pelicula peliculaInterstellar = peliculaService.findByTitulo("Interstellar").orElseThrow();
        valoraciones = valoracionService.findByPelicula(peliculaInterstellar.getIdPelicula());
        assertNotNull(valoraciones);
        assertTrue(valoraciones.isEmpty());

        // ID null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByPelicula(null);
        }, "Debe lanzar excepción cuando el ID de la película es null");

        // ID negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByPelicula(-1L);
        }, "Debe lanzar excepción cuando el ID de la película es negativo");

        // ID cero
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByPelicula(0L);
        }, "Debe lanzar excepción cuando el ID de la película es cero");
    }

    @Test
    void testFindByUsuarioAndPelicula() {
        // Valoración existe (Alicia valoró El Padrino)
        Optional<Valoracion> resultado = valoracionService.findByUsuarioAndPelicula(
                usuarioAlicia.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        );
        assertTrue(resultado.isPresent(), "Debe encontrar la valoración");
        assertEquals(7, resultado.get().getPuntuacion());

        // Valoración no existe
        Usuario usuarioMaria = usuarioService.findByUsername("maria").orElseThrow();
        resultado = valoracionService.findByUsuarioAndPelicula(
                usuarioMaria.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        );
        assertFalse(resultado.isPresent(), "No debe encontrar valoración");

        // ID usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuarioAndPelicula(null, peliculaPadrino.getIdPelicula());
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuarioAndPelicula(-1L, peliculaPadrino.getIdPelicula());
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID película null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuarioAndPelicula(usuarioAlicia.getIdUsuario(), null);
        }, "Debe lanzar excepción cuando el ID de la película es null");

        // ID película negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.findByUsuarioAndPelicula(usuarioAlicia.getIdUsuario(), -1L);
        }, "Debe lanzar excepción cuando el ID de la película es negativo");
    }

    @Test
    void testCrearOActualizarValoracion() {
        // Crear nueva valoración
        Usuario usuarioMaria = usuarioService.findByUsername("maria").orElseThrow();
        Pelicula peliculaInception = peliculaService.findByTitulo("Inception").orElseThrow();

        Valoracion nuevaValoracion = valoracionService.crearOActualizarValoracion(
                usuarioMaria.getIdUsuario(),
                peliculaInception.getIdPelicula(),
                8,
                "Excelente película"
        );
        assertNotNull(nuevaValoracion);
        assertEquals(8, nuevaValoracion.getPuntuacion());
        assertEquals("Excelente película", nuevaValoracion.getComentario());
        assertEquals(usuarioMaria.getIdUsuario(), nuevaValoracion.getUsuario().getIdUsuario());
        assertEquals(peliculaInception.getIdPelicula(), nuevaValoracion.getPelicula().getIdPelicula());

        // Actualizar valoración existente (Alicia ya valoró El Padrino con 7)
        Valoracion valoracionActualizada = valoracionService.crearOActualizarValoracion(
                usuarioAlicia.getIdUsuario(),
                peliculaPadrino.getIdPelicula(),
                10,
                "Obra maestra actualizada"
        );
        assertNotNull(valoracionActualizada);
        assertEquals(10, valoracionActualizada.getPuntuacion());
        assertEquals("Obra maestra actualizada", valoracionActualizada.getComentario());
        assertEquals(valoracionExistente.getIdValoracion(), valoracionActualizada.getIdValoracion());

        // ID usuario null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(null, peliculaPadrino.getIdPelicula(), 5, "Test");
        }, "Debe lanzar excepción cuando el ID del usuario es null");

        // ID usuario negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(-1L, peliculaPadrino.getIdPelicula(), 5, "Test");
        }, "Debe lanzar excepción cuando el ID del usuario es negativo");

        // ID película null
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(usuarioAlicia.getIdUsuario(), null, 5, "Test");
        }, "Debe lanzar excepción cuando el ID de la película es null");

        // ID película negativo
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(usuarioAlicia.getIdUsuario(), -1L, 5, "Test");
        }, "Debe lanzar excepción cuando el ID de la película es negativo");

        // Puntuación menor a 1
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(usuarioAlicia.getIdUsuario(), peliculaPadrino.getIdPelicula(), 0, "Test");
        }, "Debe lanzar excepción cuando la puntuación es menor a 1");

        // Puntuación mayor a 10
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(usuarioAlicia.getIdUsuario(), peliculaPadrino.getIdPelicula(), 11, "Test");
        }, "Debe lanzar excepción cuando la puntuación es mayor a 10");

        // Usuario no existe
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(99999L, peliculaPadrino.getIdPelicula(), 5, "Test");
        }, "Debe lanzar excepción cuando el usuario no existe");

        // Película no existe
        assertThrows(IllegalArgumentException.class, () -> {
            valoracionService.crearOActualizarValoracion(usuarioAlicia.getIdUsuario(), 99999L, 5, "Test");
        }, "Debe lanzar excepción cuando la película no existe");
    }

    @Test
    void testUsuarioYaValoroPelicula() {
        // Usuario valoró la película (Alicia valoró El Padrino)
        assertTrue(valoracionService.usuarioYaValoroPelicula(
                usuarioAlicia.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        ), "Debe retornar true cuando el usuario ya valoró la película");

        // Usuario no valoró la película
        Usuario usuarioMaria = usuarioService.findByUsername("maria").orElseThrow();
        assertFalse(valoracionService.usuarioYaValoroPelicula(
                usuarioMaria.getIdUsuario(),
                peliculaPadrino.getIdPelicula()
        ), "Debe retornar false cuando el usuario no valoró la película");

        // ID usuario null
        assertFalse(valoracionService.usuarioYaValoroPelicula(null, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el ID del usuario es null");

        // ID usuario negativo
        assertFalse(valoracionService.usuarioYaValoroPelicula(-1L, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el ID del usuario es negativo");

        // ID usuario cero
        assertFalse(valoracionService.usuarioYaValoroPelicula(0L, peliculaPadrino.getIdPelicula()),
                "Debe retornar false cuando el ID del usuario es cero");

        // ID película null
        assertFalse(valoracionService.usuarioYaValoroPelicula(usuarioAlicia.getIdUsuario(), null),
                "Debe retornar false cuando el ID de la película es null");

        // ID película negativo
        assertFalse(valoracionService.usuarioYaValoroPelicula(usuarioAlicia.getIdUsuario(), -1L),
                "Debe retornar false cuando el ID de la película es negativo");

        // ID película cero
        assertFalse(valoracionService.usuarioYaValoroPelicula(usuarioAlicia.getIdUsuario(), 0L),
                "Debe retornar false cuando el ID de la película es cero");
    }
}

