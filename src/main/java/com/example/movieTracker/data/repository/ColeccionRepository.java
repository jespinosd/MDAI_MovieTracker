package com.example.movieTracker.data.repository;

import com.example.movieTracker.data.model.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColeccionRepository extends JpaRepository<Coleccion,Long> {
}
