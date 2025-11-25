package es.unex.cum.mdai.movieTracker.data.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Página principal (redirige a login o perfil)
    @GetMapping("/")
    public String inicio(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/usuarios/perfil";
        }
        return "redirect:/usuarios/login";
    }
}

