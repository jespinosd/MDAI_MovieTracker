package com.example.movieTracker.data.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Coleccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idColeccion;

    @OneToOne
    // clave foránea:idUsuario
    private Usuario usuario;

    @ManyToMany
    private List<Pelicula> listaPeliculas = new ArrayList<>();

    // Constructors
    public Coleccion() {}

    public Coleccion(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters and Setters
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
