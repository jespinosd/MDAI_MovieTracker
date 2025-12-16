package es.unex.cum.mdai.movieTracker.data.controllers;

import es.unex.cum.mdai.movieTracker.data.dto.ChatRequest;
import es.unex.cum.mdai.movieTracker.data.dto.ChatResponse;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.services.ChatbotService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    // Procesar mensaje y devolver respuesta (REST endpoint para AJAX)
    @PostMapping("/mensaje")
    public ChatResponse procesarMensaje(@RequestBody ChatRequest request, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return new ChatResponse("Por favor, inicia sesión para usar el chat de recomendaciones.");
        }

        return chatbotService.obtenerRecomendacion(request, usuario.getIdUsuario());
    }
}
