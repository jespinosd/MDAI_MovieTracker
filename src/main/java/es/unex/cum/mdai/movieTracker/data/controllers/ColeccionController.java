package es.unex.cum.mdai.movieTracker.data.controllers;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.services.ColeccionService;
import es.unex.cum.mdai.movieTracker.data.services.ValoracionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/coleccion")
public class ColeccionController {

    private final ColeccionService coleccionService;
    private final ValoracionService valoracionService;

    @Autowired
    public ColeccionController(ColeccionService coleccionService, ValoracionService valoracionService) {
        this.coleccionService = coleccionService;
        this.valoracionService = valoracionService;
    }

    // Página principal de Mi Colección (overview)
    @GetMapping
    public String verMiColeccion(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tu colección");
            return "redirect:/usuarios/login";
        }

        List<Pelicula> peliculas = coleccionService.obtenerPeliculasDeColeccion(usuario.getIdUsuario());

        // Separar en vistas y pendientes
        List<Pelicula> peliculasVistas = peliculas.stream()
                .filter(pelicula -> valoracionService.usuarioYaValoroPelicula(usuario.getIdUsuario(), pelicula.getIdPelicula()))
                .collect(Collectors.toList());

        List<Pelicula> peliculasPendientes = peliculas.stream()
                .filter(pelicula -> !valoracionService.usuarioYaValoroPelicula(usuario.getIdUsuario(), pelicula.getIdPelicula()))
                .collect(Collectors.toList());

        model.addAttribute("totalPeliculas", peliculas.size());
        model.addAttribute("totalVistas", peliculasVistas.size());
        model.addAttribute("totalPendientes", peliculasPendientes.size());
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "mi-coleccion";
    }

    // Ver todas las películas de la colección
    @GetMapping("/todas")
    public String verTodasLasPeliculas(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tu colección");
            return "redirect:/usuarios/login";
        }

        List<Pelicula> peliculas = coleccionService.obtenerPeliculasDeColeccion(usuario.getIdUsuario());

        model.addAttribute("peliculas", peliculas);
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "coleccion-todas";
    }

    // Ver películas vistas (con valoración)
    @GetMapping("/vistas")
    public String verPeliculasVistas(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tus películas vistas");
            return "redirect:/usuarios/login";
        }

        List<Valoracion> valoraciones = valoracionService.findByUsuario(usuario.getIdUsuario());

        model.addAttribute("valoraciones", valoraciones);
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "peliculas-vistas";
    }

    // Ver películas pendientes (en colección pero sin valorar)
    @GetMapping("/pendientes")
    public String verPeliculasPendientes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tus películas pendientes");
            return "redirect:/usuarios/login";
        }

        // Obtener todas las películas de la colección del usuario
        List<Pelicula> peliculasEnColeccion = coleccionService.obtenerPeliculasDeColeccion(usuario.getIdUsuario());

        // Filtrar las que NO tienen valoración
        List<Pelicula> peliculasPendientes = peliculasEnColeccion.stream()
                .filter(pelicula -> !valoracionService.usuarioYaValoroPelicula(usuario.getIdUsuario(), pelicula.getIdPelicula()))
                .collect(Collectors.toList());

        model.addAttribute("peliculas", peliculasPendientes);
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "peliculas-pendientes";
    }

    // Agregar película a la colección
    @PostMapping("/agregar/{idPelicula}")
    public String agregarPeliculaAColeccion(@PathVariable Long idPelicula,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para agregar películas a tu colección");
            return "redirect:/usuarios/login";
        }

        try {
            coleccionService.agregarPeliculaAColeccion(usuario.getIdUsuario(), idPelicula);
            redirectAttributes.addFlashAttribute("mensaje", "Película agregada a tu colección exitosamente");
            return "redirect:/peliculas/" + idPelicula;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/peliculas/" + idPelicula;
        }
    }

    // Eliminar película de la colección
    @PostMapping("/eliminar/{idPelicula}")
    public String eliminarPeliculaDeColeccion(@PathVariable Long idPelicula,
                                             @RequestParam(required = false) String origen,
                                             HttpSession session,
                                             RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para eliminar películas de tu colección");
            return "redirect:/usuarios/login";
        }

        try {
            coleccionService.eliminarPeliculaDeColeccion(usuario.getIdUsuario(), idPelicula);
            redirectAttributes.addFlashAttribute("mensaje", "Película eliminada de tu colección exitosamente");

            // Redirigir según el origen
            if ("detalle".equals(origen)) {
                return "redirect:/peliculas/" + idPelicula;
            } else if ("pendientes".equals(origen)) {
                return "redirect:/coleccion/pendientes";
            } else if ("vistas".equals(origen)) {
                return "redirect:/coleccion/vistas";
            } else {
                return "redirect:/coleccion/todas";
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/coleccion";
        }
    }
}

