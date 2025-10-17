package com.example.movieTracker.data.repository;

import com.example.movieTracker.data.model.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeliculaRepository extends JpaRepository<Pelicula,Long> {
}
