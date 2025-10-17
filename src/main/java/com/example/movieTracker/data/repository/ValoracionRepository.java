package com.example.movieTracker.data.repository;

import com.example.movieTracker.data.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValoracionRepository extends JpaRepository<Valoracion,Long> {
}
