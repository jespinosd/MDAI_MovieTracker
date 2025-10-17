package com.example.movieTracker.data.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pelicula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPelicula;
    private String titulo;
    private int anio;
    private String genero;
    private String sinopsis;
    private String pathImagen;

    @ManyToMany(mappedBy = "listaPeliculas")
    private List<Coleccion> listaColecciones = new ArrayList<>();

    @OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL)
    private List<Valoracion> listaValoraciones = new ArrayList<>();

    // Constructors
    public Pelicula() {}

    public Pelicula(String titulo, int anio, String genero, String sinopsis, String pathImagen) {
        this.titulo = titulo;
        this.anio = anio;
        this.genero = genero;
        this.sinopsis = sinopsis;
        this.pathImagen = pathImagen;
    }

    // Getters and Setters
    public Long getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(Long idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getPathImagen() {
        return pathImagen;
    }

    public void setPathImagen(String pathImagen) {
        this.pathImagen = pathImagen;
    }

    public List<Coleccion> getListaColecciones() {
        return listaColecciones;
    }

    public void setListaColecciones(List<Coleccion> listaColecciones) {
        this.listaColecciones = listaColecciones;
    }

    public List<Valoracion> getListaValoraciones() {
        return listaValoraciones;
    }

    public void setListaValoraciones(List<Valoracion> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
    }
}
