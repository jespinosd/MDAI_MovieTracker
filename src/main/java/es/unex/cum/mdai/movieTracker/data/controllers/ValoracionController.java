package es.unex.cum.mdai.movieTracker.data.controllers;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.model.Valoracion;
import es.unex.cum.mdai.movieTracker.data.services.ColeccionService;
import es.unex.cum.mdai.movieTracker.data.services.PeliculaService;
import es.unex.cum.mdai.movieTracker.data.services.ValoracionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;
    private final PeliculaService peliculaService;
    private final ColeccionService coleccionService;

    @Autowired
    public ValoracionController(ValoracionService valoracionService, PeliculaService peliculaService, ColeccionService coleccionService) {
        this.valoracionService = valoracionService;
        this.peliculaService = peliculaService;
        this.coleccionService = coleccionService;
    }

    // Listar todas las valoraciones del usuario logueado (películas vistas)
    @GetMapping("/vistas")
    public String listarPeliculasVistas(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tus películas vistas");
            return "redirect:/usuarios/login";
        }

        List<Valoracion> valoraciones = valoracionService.findByUsuario(usuario.getIdUsuario());

        model.addAttribute("valoraciones", valoraciones);
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "coleccion-vistas";
    }

    // Listar películas pendientes (en colección pero sin valorar)
    @GetMapping("/pendientes")
    public String listarPeliculasPendientes(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para ver tus películas pendientes");
            return "redirect:/usuarios/login";
        }

        // Redirigir a la ruta de colección
        return "redirect:/coleccion/pendientes";
    }

    // Mostrar formulario para agregar/editar valoración
    @GetMapping("/agregar/{idPelicula}")
    public String mostrarFormularioValoracion(@PathVariable Long idPelicula,
                                             HttpSession session,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para valorar películas");
            return "redirect:/usuarios/login";
        }

        Optional<Pelicula> peliculaOpt = peliculaService.findById(idPelicula);

        if (peliculaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Película no encontrada");
            return "redirect:/peliculas";
        }

        Pelicula pelicula = peliculaOpt.get();

        // Verificar que la película está en la colección del usuario
        if (!coleccionService.peliculaEstaEnColeccion(usuario.getIdUsuario(), idPelicula)) {
            redirectAttributes.addFlashAttribute("error", "Debes agregar la película a tu colección antes de valorarla");
            return "redirect:/peliculas/" + idPelicula;
        }

        // Verificar si ya existe una valoración
        Optional<Valoracion> valoracionExistente = valoracionService.findByUsuarioAndPelicula(
                usuario.getIdUsuario(), idPelicula);

        model.addAttribute("pelicula", pelicula);
        model.addAttribute("valoracion", valoracionExistente.orElse(new Valoracion()));
        model.addAttribute("yaValorada", valoracionExistente.isPresent());
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "agregar-valoracion";
    }

    // Procesar el formulario de valoración
    @PostMapping("/agregar/{idPelicula}")
    public String agregarValoracion(@PathVariable Long idPelicula,
                                   @RequestParam int puntuacion,
                                   @RequestParam(required = false) String comentario,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para valorar películas");
            return "redirect:/usuarios/login";
        }

        // Verificar que la película está en la colección del usuario
        if (!coleccionService.peliculaEstaEnColeccion(usuario.getIdUsuario(), idPelicula)) {
            redirectAttributes.addFlashAttribute("error", "Debes agregar la película a tu colección antes de valorarla");
            return "redirect:/peliculas/" + idPelicula;
        }

        try {
            Valoracion valoracion = valoracionService.crearOActualizarValoracion(
                    usuario.getIdUsuario(), idPelicula, puntuacion, comentario);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Valoración guardada exitosamente");
            return "redirect:/peliculas/" + idPelicula;

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/valoraciones/agregar/" + idPelicula;
        }
    }

    // Editar una valoración existente
    @GetMapping("/editar/{idValoracion}")
    public String mostrarFormularioEdicion(@PathVariable Long idValoracion,
                                          HttpSession session,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para editar valoraciones");
            return "redirect:/usuarios/login";
        }

        Optional<Valoracion> valoracionOpt = valoracionService.findById(idValoracion);

        if (valoracionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Valoración no encontrada");
            return "redirect:/valoraciones/vistas";
        }

        Valoracion valoracion = valoracionOpt.get();

        // Verificar que la valoración pertenece al usuario logueado
        if (!valoracion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta valoración");
            return "redirect:/valoraciones/vistas";
        }

        model.addAttribute("valoracion", valoracion);
        model.addAttribute("pelicula", valoracion.getPelicula());
        model.addAttribute("usuario", usuario);
        model.addAttribute("logueado", true);

        return "editar-valoracion";
    }

    // Procesar edición de valoración
    @PostMapping("/editar/{idValoracion}")
    public String editarValoracion(@PathVariable Long idValoracion,
                                  @RequestParam int puntuacion,
                                  @RequestParam(required = false) String comentario,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para editar valoraciones");
            return "redirect:/usuarios/login";
        }

        try {
            Optional<Valoracion> valoracionOpt = valoracionService.findById(idValoracion);

            if (valoracionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Valoración no encontrada");
                return "redirect:/valoraciones/vistas";
            }

            Valoracion valoracion = valoracionOpt.get();

            // Verificar que la valoración pertenece al usuario logueado
            if (!valoracion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta valoración");
                return "redirect:/valoraciones/vistas";
            }

            valoracion.setPuntuacion(puntuacion);
            valoracion.setComentario(comentario);
            valoracionService.save(valoracion);

            redirectAttributes.addFlashAttribute("mensaje", "Valoración actualizada exitosamente");
            return "redirect:/peliculas/" + valoracion.getPelicula().getIdPelicula();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/valoraciones/editar/" + idValoracion;
        }
    }

    // Eliminar una valoración
    @PostMapping("/eliminar/{idValoracion}")
    public String eliminarValoracion(@PathVariable Long idValoracion,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para eliminar valoraciones");
            return "redirect:/usuarios/login";
        }

        try {
            Optional<Valoracion> valoracionOpt = valoracionService.findById(idValoracion);

            if (valoracionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Valoración no encontrada");
                return "redirect:/valoraciones/vistas";
            }

            Valoracion valoracion = valoracionOpt.get();

            // Verificar que la valoración pertenece al usuario logueado
            if (!valoracion.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta valoración");
                return "redirect:/valoraciones/vistas";
            }

            valoracionService.deleteById(idValoracion);

            redirectAttributes.addFlashAttribute("mensaje", "Valoración eliminada exitosamente");
            return "redirect:/valoraciones/vistas";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/valoraciones/vistas";
        }
    }
}
