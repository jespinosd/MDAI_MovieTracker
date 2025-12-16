package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.dto.ChatRequest;
import es.unex.cum.mdai.movieTracker.data.dto.ChatResponse;

public interface ChatbotService {
    ChatResponse obtenerRecomendacion(ChatRequest chatRequest, Long idUsuario);
}

