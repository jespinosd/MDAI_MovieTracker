package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.dto.*;
import es.unex.cum.mdai.movieTracker.data.model.Coleccion;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    private final WebClient webClient;
    private final PeliculaService peliculaService;
    private final UsuarioService usuarioService;
    private final ColeccionService coleccionService;
    private final ValoracionService valoracionService;

    @Autowired
    public ChatbotServiceImpl(WebClient.Builder webClientBuilder,
                              PeliculaService peliculaService,
                              UsuarioService usuarioService,
                              ColeccionService coleccionService,
                              ValoracionService valoracionService) {
        this.webClient = webClientBuilder.build();
        this.peliculaService = peliculaService;
        this.usuarioService = usuarioService;
        this.coleccionService = coleccionService;
        this.valoracionService = valoracionService;
    }

    @Override
    public ChatResponse obtenerRecomendacion(ChatRequest chatRequest, Long idUsuario) {
        try {
            // Obtener el contexto del usuario
            String contexto = construirContextoUsuario(idUsuario);

            // Construir el prompt del sistema
            String systemPrompt = "Eres un asistente experto en recomendaciones de películas. " +
                    "Tu trabajo es recomendar películas del catálogo disponible basándote en los gustos del usuario. " +
                    "Responde de forma amigable, concisa y útil en español. " +
                    "Si el usuario pregunta sobre películas que no están en el catálogo, menciónalo educadamente. " +
                    "Enfócate en recomendar películas que realmente existen en nuestro catálogo.\n\n" +
                    contexto;

            // Crear los mensajes
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", chatRequest.getMensaje()));

            // Crear la petición a OpenRouter
            OpenRouterRequest request = new OpenRouterRequest(model, messages);

            // Realizar la llamada a la API
            OpenRouterResponse response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenRouterResponse.class)
                    .block();

            // Extraer la respuesta
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String respuesta = response.getChoices().get(0).getMessage().getContent();
                return new ChatResponse(respuesta);
            } else {
                return new ChatResponse("Lo siento, no pude generar una recomendación en este momento. Por favor, intenta de nuevo.");
            }

        } catch (Exception e) {
            return new ChatResponse("Ocurrió un error al procesar tu solicitud. Por favor, intenta de nuevo más tarde.");
        }
    }

    private String construirContextoUsuario(Long idUsuario) {
        StringBuilder contexto = new StringBuilder();

        // Obtener todas las películas del catálogo
        List<Pelicula> catalogoPeliculas = peliculaService.findAll();
        contexto.append("CATÁLOGO DE PELÍCULAS DISPONIBLE:\n");
        for (Pelicula p : catalogoPeliculas) {
            contexto.append("- ").append(p.getTitulo())
                    .append(" (").append(p.getAnio()).append(") - ")
                    .append("Director: ").append(p.getDirector())
                    .append(", Género: ").append(p.getGenero());

            // Agregar valoración media si existe
            Double valoracionMedia = peliculaService.obtenerValoracionMedia(p.getIdPelicula());
            if (valoracionMedia != null && valoracionMedia > 0) {
                contexto.append(", Valoración media: ").append(String.format("%.1f", valoracionMedia));
            }
            contexto.append("\n");
        }

        // Si hay un usuario logueado, agregar su información
        if (idUsuario != null) {
            Optional<Usuario> usuarioOpt = usuarioService.findById(idUsuario);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                // Obtener la colección del usuario
                Optional<Coleccion> coleccionOpt = coleccionService.findByUsuario(idUsuario);
                if (coleccionOpt.isPresent()) {
                    Coleccion coleccion = coleccionOpt.get();
                    List<Pelicula> peliculasUsuario = coleccion.getListaPeliculas();

                    if (!peliculasUsuario.isEmpty()) {
                        contexto.append("\n\nPELÍCULAS EN LA COLECCIÓN DEL USUARIO:\n");
                        for (Pelicula p : peliculasUsuario) {
                            contexto.append("- ").append(p.getTitulo()).append(" (").append(p.getAnio()).append(")\n");
                        }
                    }
                }

                // Obtener las valoraciones del usuario
                List<Valoracion> valoraciones = valoracionService.findByUsuario(idUsuario);
                if (!valoraciones.isEmpty()) {
                    contexto.append("\nVALORACIONES DEL USUARIO:\n");
                    for (Valoracion v : valoraciones) {
                        Pelicula p = v.getPelicula();
                        if (p != null) {
                            contexto.append("- ").append(p.getTitulo())
                                    .append(": ").append(v.getPuntuacion()).append("/10");
                            if (v.getComentario() != null && !v.getComentario().isEmpty()) {
                                contexto.append(" - \"").append(v.getComentario()).append("\"");
                            }
                            contexto.append("\n");
                        }
                    }
                }
            }
        }

        return contexto.toString();
    }
}
