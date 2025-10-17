package com.example.movieTracker.data.repository;

import com.example.movieTracker.data.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
}
