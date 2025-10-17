package es.unex.cum.mdai.movieTracker.data.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colecciones")
public class Coleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idColeccion;

    @OneToOne
    private Usuario usuario;

    @ManyToMany
    private List<Pelicula> listaPeliculas = new ArrayList<>();

    // Constructor por defecto
    public Coleccion() {
    }

    // Constructor con parámetros
    public Coleccion(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getIdColeccion() {
        return idColeccion;
    }

    public void setIdColeccion(Long idColeccion) {
        this.idColeccion = idColeccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    public void setListaPeliculas(List<Pelicula> listaPeliculas) {
        this.listaPeliculas = listaPeliculas;
    }
}
