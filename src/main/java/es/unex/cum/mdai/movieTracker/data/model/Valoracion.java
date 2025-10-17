package es.unex.cum.mdai.movieTracker.data.model;

import jakarta.persistence.*;

@Entity
@Table(name = "valoraciones")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idValoracion;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Pelicula pelicula;

    @Column(nullable = false)
    private int puntuacion;

    @Column(length = 500)
    private String comentario;

    // Constructor por defecto
    public Valoracion() {
    }

    // Constructor con parámetros
    public Valoracion(Usuario usuario, Pelicula pelicula, int puntuacion, String comentario) {
        this.usuario = usuario;
        this.pelicula = pelicula;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    // Getters y Setters
    public Long getIdValoracion() {
        return idValoracion;
    }

    public void setIdValoracion(Long idValoracion) {
        this.idValoracion = idValoracion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
