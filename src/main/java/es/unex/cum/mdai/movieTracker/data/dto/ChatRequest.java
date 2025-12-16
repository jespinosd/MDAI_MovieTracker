package es.unex.cum.mdai.movieTracker.data.dto;

public class ChatRequest {
    private String mensaje;

    public ChatRequest() {
    }

    public ChatRequest(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

