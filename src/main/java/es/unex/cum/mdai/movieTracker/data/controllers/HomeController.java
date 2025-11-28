package es.unex.cum.mdai.movieTracker.data.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Página principal (siempre redirige al catálogo de películas)
    @GetMapping("/")
    public String inicio(HttpSession session) {
        return "redirect:/peliculas";
    }
}
