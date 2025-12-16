package es.unex.cum.mdai.movieTracker;

import es.unex.cum.mdai.movieTracker.data.dto.ChatRequest;
import es.unex.cum.mdai.movieTracker.data.dto.ChatResponse;
import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ChatbotServiceTest {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private ColeccionService coleccionService;

    @Autowired
    private ValoracionService valoracionService;

    private Usuario usuarioAlicia;
    private Usuario usuarioJuan;

    @BeforeEach
    void setUp() {
        // Recuperar usuarios de prueba
        usuarioAlicia = usuarioService.findByUsername("alicia").orElse(null);
        assertNotNull(usuarioAlicia, "El usuario Alicia debe existir");

        usuarioJuan = usuarioService.findByUsername("juanp").orElse(null);
        assertNotNull(usuarioJuan, "El usuario Juan debe existir");
    }

    @Test
    void testObtenerRecomendacionConUsuarioLogueado() {
        // Given: Un usuario logueado con colección y valoraciones
        ChatRequest request = new ChatRequest("¿Qué películas de ciencia ficción me recomiendas?");

        // When: Se solicita una recomendación
        ChatResponse response = chatbotService.obtenerRecomendacion(request, usuarioAlicia.getIdUsuario());

        // Then: Se debe recibir una respuesta válida
        assertNotNull(response, "La respuesta no debe ser null");
        assertNotNull(response.getRespuesta(), "La respuesta debe contener texto");
        assertFalse(response.getRespuesta().isEmpty(), "La respuesta no debe estar vacía");

        // Verificar que no es un mensaje de error genérico
        assertFalse(response.getRespuesta().contains("Ocurrió un error"),
                "La respuesta no debería ser un mensaje de error genérico");
    }

    @Test
    void testObtenerRecomendacionConUsuarioNull() {
        // Given: No hay usuario logueado
        ChatRequest request = new ChatRequest("¿Qué películas me recomiendas?");

        // When: Se solicita una recomendación sin usuario
        ChatResponse response = chatbotService.obtenerRecomendacion(request, null);

        // Then: Se debe recibir una respuesta válida basada solo en el catálogo
        assertNotNull(response, "La respuesta no debe ser null");
        assertNotNull(response.getRespuesta(), "La respuesta debe contener texto");
        assertFalse(response.getRespuesta().isEmpty(), "La respuesta no debe estar vacía");
    }

    @Test
    void testObtenerRecomendacionConMensajeVacio() {
        // Given: Un mensaje vacío
        ChatRequest request = new ChatRequest("");

        // When: Se solicita una recomendación con mensaje vacío
        ChatResponse response = chatbotService.obtenerRecomendacion(request, usuarioAlicia.getIdUsuario());

        // Then: Se debe recibir una respuesta válida
        assertNotNull(response, "La respuesta no debe ser null");
        assertNotNull(response.getRespuesta(), "La respuesta debe contener texto");
    }

    @Test
    void testObtenerRecomendacionConDiferentesTiposDePreguntas() {
        // Test 1: Pregunta sobre género específico
        ChatRequest request1 = new ChatRequest("¿Tienes películas de terror?");
        ChatResponse response1 = chatbotService.obtenerRecomendacion(request1, usuarioAlicia.getIdUsuario());
        assertNotNull(response1.getRespuesta());
        assertFalse(response1.getRespuesta().isEmpty());

        // Test 2: Pregunta sobre director
        ChatRequest request2 = new ChatRequest("¿Qué películas de Christopher Nolan tienes?");
        ChatResponse response2 = chatbotService.obtenerRecomendacion(request2, usuarioAlicia.getIdUsuario());
        assertNotNull(response2.getRespuesta());
        assertFalse(response2.getRespuesta().isEmpty());

        // Test 3: Pregunta abierta
        ChatRequest request3 = new ChatRequest("Sorpréndeme con algo bueno");
        ChatResponse response3 = chatbotService.obtenerRecomendacion(request3, usuarioAlicia.getIdUsuario());
        assertNotNull(response3.getRespuesta());
        assertFalse(response3.getRespuesta().isEmpty());
    }

    @Test
    void testServicioTieneAccesoADatosDeUsuario() {
        // Verificar que el servicio puede acceder a los datos necesarios

        // Verificar películas del catálogo
        List<Pelicula> peliculas = peliculaService.findAll();
        assertNotNull(peliculas, "Debe haber películas en el catálogo");
        assertFalse(peliculas.isEmpty(), "El catálogo no debe estar vacío");

        // Verificar colección del usuario
        Coleccion coleccion = coleccionService.findByUsuario(usuarioAlicia.getIdUsuario()).orElse(null);
        assertNotNull(coleccion, "El usuario debe tener una colección");

        // Verificar valoraciones del usuario
        List<Valoracion> valoraciones = valoracionService.findByUsuario(usuarioAlicia.getIdUsuario());
        assertNotNull(valoraciones, "Debe poder obtener las valoraciones del usuario");
    }

    @Test
    void testRespuestaNoEsNull() {
        // Given: Cualquier petición válida
        ChatRequest request = new ChatRequest("Hola");

        // When: Se procesa la petición
        ChatResponse response = chatbotService.obtenerRecomendacion(request, usuarioAlicia.getIdUsuario());

        // Then: La respuesta nunca debe ser null
        assertNotNull(response, "El servicio siempre debe devolver un objeto ChatResponse");
        assertNotNull(response.getRespuesta(), "La respuesta debe contener algún mensaje");
    }
}

