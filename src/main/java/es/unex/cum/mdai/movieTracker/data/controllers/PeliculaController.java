package es.unex.cum.mdai.movieTracker.data.controllers;

import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.services.PeliculaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/peliculas")
public class PeliculaController {

    private final PeliculaService peliculaService;

    @Autowired
    public PeliculaController(PeliculaService peliculaService) {
        this.peliculaService = peliculaService;
    }

    // Página principal - Catálogo de películas
    @GetMapping
    public String listarPeliculas(@RequestParam(required = false) String query,
                                  @RequestParam(required = false) String tipo,
                                  @RequestParam(required = false) String genero,
                                  @RequestParam(required = false) Integer anio,
                                  @RequestParam(required = false) String ordenar,
                                  HttpSession session,
                                  Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        // No redirigir al login, permitir acceso sin autenticación

        List<Pelicula> peliculas;

        // Búsqueda por query
        if (query != null && !query.trim().isEmpty()) {
            peliculas = peliculaService.buscar(query, tipo);
            model.addAttribute("query", query);
            model.addAttribute("tipo", tipo);
        }
        // Filtrado por género y/o año
        else if ((genero != null && !genero.isEmpty() && !"todos".equals(genero)) ||
                 (anio != null && anio > 0)) {
            peliculas = peliculaService.filtrarPorGeneroYAnio(genero, anio);
            model.addAttribute("generoSeleccionado", genero);
            model.addAttribute("anioSeleccionado", anio);
        }
        // Listado completo
        else {
            peliculas = peliculaService.findAll();
        }

        // Ordenar resultados
        if (ordenar != null) {
            peliculas = ordenarPeliculas(peliculas, ordenar);
            model.addAttribute("ordenar", ordenar);
        }

        // Obtener géneros únicos para el filtro
        List<String> generos = peliculaService.findAll().stream()
                .map(Pelicula::getGenero)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Obtener años únicos para el filtro
        List<Integer> anios = peliculaService.findAll().stream()
                .map(Pelicula::getAnio)
                .distinct()
                .sorted((a, b) -> b - a) // Orden descendente
                .collect(Collectors.toList());

        model.addAttribute("peliculas", peliculas);
        model.addAttribute("generos", generos);
        model.addAttribute("anios", anios);
        model.addAttribute("usuario", usuario); // Puede ser null si no está logueado
        model.addAttribute("logueado", usuario != null); // Indicador de si está logueado

        return "catalogo-peliculas";
    }

    // Ver detalle de una película
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        // Permitir ver detalles sin login

        try {
            Optional<Pelicula> peliculaOpt = peliculaService.findById(id);
            if (peliculaOpt.isPresent()) {
                Pelicula pelicula = peliculaOpt.get();
                Double valoracionMedia = peliculaService.obtenerValoracionMedia(id);

                model.addAttribute("pelicula", pelicula);
                model.addAttribute("valoracionMedia", valoracionMedia);
                model.addAttribute("usuario", usuario); // Puede ser null
                model.addAttribute("logueado", usuario != null);
                return "detalle-pelicula";
            } else {
                redirectAttributes.addFlashAttribute("error", "Película no encontrada");
                return "redirect:/peliculas";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar los detalles de la película");
            return "redirect:/peliculas";
        }
    }

    // Mostrar formulario para agregar película
    @GetMapping("/agregar")
    public String mostrarFormularioAgregar(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("usuario", usuario);
        return "agregar-pelicula";
    }

    // Procesar creación de película
    @PostMapping("/agregar")
    public String agregarPelicula(@ModelAttribute Pelicula pelicula,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        try {
            peliculaService.save(pelicula);
            redirectAttributes.addFlashAttribute("mensaje", "Película agregada correctamente");
            return "redirect:/peliculas";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/peliculas/agregar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al agregar la película");
            return "redirect:/peliculas/agregar";
        }
    }

    // Mostrar formulario para editar película
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                         HttpSession session,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        try {
            Optional<Pelicula> peliculaOpt = peliculaService.findById(id);
            if (peliculaOpt.isPresent()) {
                model.addAttribute("pelicula", peliculaOpt.get());
                model.addAttribute("usuario", usuario);
                return "editar-pelicula";
            } else {
                redirectAttributes.addFlashAttribute("error", "Película no encontrada");
                return "redirect:/peliculas";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar la película");
            return "redirect:/peliculas";
        }
    }

    // Procesar edición de película
    @PostMapping("/editar/{id}")
    public String editarPelicula(@PathVariable Long id,
                                @ModelAttribute Pelicula pelicula,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        try {
            pelicula.setIdPelicula(id);
            peliculaService.save(pelicula);
            redirectAttributes.addFlashAttribute("mensaje", "Película actualizada correctamente");
            return "redirect:/peliculas/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/peliculas/editar/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar la película");
            return "redirect:/peliculas/editar/" + id;
        }
    }

    // Eliminar película
    @PostMapping("/eliminar/{id}")
    public String eliminarPelicula(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        try {
            peliculaService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Película eliminada correctamente");
            return "redirect:/peliculas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la película: " + e.getMessage());
            return "redirect:/peliculas";
        }
    }

    // Método auxiliar para ordenar películas
    private List<Pelicula> ordenarPeliculas(List<Pelicula> peliculas, String criterio) {
        return switch (criterio) {
            case "titulo-asc" -> peliculas.stream()
                    .sorted((p1, p2) -> p1.getTitulo().compareToIgnoreCase(p2.getTitulo()))
                    .collect(Collectors.toList());
            case "titulo-desc" -> peliculas.stream()
                    .sorted((p1, p2) -> p2.getTitulo().compareToIgnoreCase(p1.getTitulo()))
                    .collect(Collectors.toList());
            case "anio-asc" -> peliculas.stream()
                    .sorted((p1, p2) -> Integer.compare(p1.getAnio(), p2.getAnio()))
                    .collect(Collectors.toList());
            case "anio-desc" -> peliculas.stream()
                    .sorted((p1, p2) -> Integer.compare(p2.getAnio(), p1.getAnio()))
                    .collect(Collectors.toList());
            case "valoracion" -> peliculas.stream()
                    .sorted((p1, p2) -> {
                        Double v1 = peliculaService.obtenerValoracionMedia(p1.getIdPelicula());
                        Double v2 = peliculaService.obtenerValoracionMedia(p2.getIdPelicula());
                        return Double.compare(v2, v1); // Descendente
                    })
                    .collect(Collectors.toList());
            default -> peliculas;
        };
    }
}
