package es.unex.cum.mdai.movieTracker.data.controllers;

import es.unex.cum.mdai.movieTracker.data.dto.OmdbMovieDTO;
import es.unex.cum.mdai.movieTracker.data.dto.OmdbSearchResponseDTO;
import es.unex.cum.mdai.movieTracker.data.model.Pelicula;
import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.services.OmdbApiService;
import es.unex.cum.mdai.movieTracker.data.services.PeliculaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controlador para gestionar búsquedas de películas en la API externa de OMDb
 */
@Controller
@RequestMapping("/omdb")
public class OmdbController {

    @Autowired
    private OmdbApiService omdbApiService;

    @Autowired
    private PeliculaService peliculaService;

    /**
     * Muestra el formulario de búsqueda de películas externas
     */
    @GetMapping("/buscar")
    public String mostrarFormularioBusqueda(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "buscar-omdb";
    }

    /**
     * Realiza la búsqueda de películas en OMDb
     */
    @PostMapping("/buscar")
    public String buscarPeliculas(
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        if (titulo == null || titulo.trim().isEmpty()) {
            model.addAttribute("error", "Por favor, introduce un título para buscar");
            model.addAttribute("usuario", usuario);
            return "buscar-omdb";
        }

        Optional<OmdbSearchResponseDTO> searchResult = omdbApiService.searchMovies(titulo, page);

        if (searchResult.isPresent() && searchResult.get().getSearch() != null) {
            model.addAttribute("resultados", searchResult.get().getSearch());
            model.addAttribute("totalResultados", searchResult.get().getTotalResults());
            model.addAttribute("terminoBusqueda", titulo);
            model.addAttribute("paginaActual", page);
        } else {
            model.addAttribute("mensaje", "No se encontraron resultados para: " + titulo);
        }

        model.addAttribute("usuario", usuario);
        return "buscar-omdb";
    }

    /**
     * Muestra los detalles de una película de OMDb
     */
    @GetMapping("/detalle/{imdbId}")
    public String verDetallePelicula(
            @PathVariable String imdbId,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<OmdbMovieDTO> movieOpt = omdbApiService.getMovieByImdbId(imdbId);

        if (movieOpt.isPresent()) {
            model.addAttribute("pelicula", movieOpt.get());
            model.addAttribute("usuario", usuario);
            return "detalle-omdb";
        } else {
            model.addAttribute("error", "No se pudieron obtener los detalles de la película");
            return "redirect:/omdb/buscar";
        }
    }

    /**
     * Importa una película de OMDb al catálogo local (solo para ADMIN)
     */
    @PostMapping("/importar/{imdbId}")
    public String importarPelicula(
            @PathVariable String imdbId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        // Verificar que el usuario es ADMIN
        if (!"ADMIN".equals(usuario.getRol().name())) {
            redirectAttributes.addFlashAttribute("error", "Solo los administradores pueden importar películas");
            return "redirect:/omdb/detalle/" + imdbId;
        }

        Optional<OmdbMovieDTO> movieOpt = omdbApiService.getMovieByImdbId(imdbId);

        if (movieOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo obtener la información de la película");
            return "redirect:/omdb/buscar";
        }

        OmdbMovieDTO omdbMovie = movieOpt.get();

        // Verificar si la película ya existe en el catálogo
        Optional<Pelicula> existente = peliculaService.findByTitulo(omdbMovie.getTitle());
        if (existente.isPresent()) {
            redirectAttributes.addFlashAttribute("mensaje", "Esta película ya existe en el catálogo");
            return "redirect:/peliculas/" + existente.get().getIdPelicula();
        }

        // Crear nueva película en el catálogo local
        Pelicula nuevaPelicula = new Pelicula();
        nuevaPelicula.setTitulo(omdbMovie.getTitle());
        nuevaPelicula.setAnio(omdbMovie.getYearAsInt());
        nuevaPelicula.setDirector(omdbMovie.getDirector() != null ? omdbMovie.getDirector() : "Desconocido");
        nuevaPelicula.setGenero(omdbMovie.getGenre() != null ? omdbMovie.getGenre() : "Sin clasificar");
        nuevaPelicula.setSinopsis(omdbMovie.getPlot() != null ? omdbMovie.getPlot() : "Sin sinopsis");

        // Usar el póster de OMDb si está disponible y no es "N/A"
        if (omdbMovie.getPoster() != null && !"N/A".equals(omdbMovie.getPoster())) {
            nuevaPelicula.setPathImagen(omdbMovie.getPoster());
        } else {
            nuevaPelicula.setPathImagen("/img/default-movie.jpg");
        }

        Pelicula peliculaGuardada = peliculaService.save(nuevaPelicula);

        redirectAttributes.addFlashAttribute("mensaje", "Película importada exitosamente al catálogo");
        return "redirect:/peliculas/" + peliculaGuardada.getIdPelicula();
    }
}

