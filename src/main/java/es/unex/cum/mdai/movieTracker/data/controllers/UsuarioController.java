package es.unex.cum.mdai.movieTracker.data.controllers;

import es.unex.cum.mdai.movieTracker.data.model.Usuario;
import es.unex.cum.mdai.movieTracker.data.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Página de inicio / Login
    @GetMapping("/login")
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/usuarios/perfil";
        }
        return "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.login(username, password);

            if (usuarioOpt.isPresent()) {
                session.setAttribute("usuarioLogueado", usuarioOpt.get());
                redirectAttributes.addFlashAttribute("mensaje", "¡Bienvenido, " + usuarioOpt.get().getNombre() + "!");
                return "redirect:/usuarios/perfil";
            } else {
                redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
                return "redirect:/usuarios/login";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al iniciar sesión");
            return "redirect:/usuarios/login";
        }
    }

    // Página de registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/usuarios/perfil";
        }
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        try {
            Usuario registrado = usuarioService.registrar(usuario);
            session.setAttribute("usuarioLogueado", registrado);
            redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. ¡Bienvenido!");
            return "redirect:/usuarios/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/registro";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al registrar usuario");
            return "redirect:/usuarios/registro";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Has cerrado sesión correctamente");
        return "redirect:/usuarios/login";
    }

    // Ver perfil
    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        try {
            // Recargar desde BD para tener datos actualizados
            Optional<Usuario> usuarioActualizado = usuarioService.findById(usuario.getIdUsuario());
            if (usuarioActualizado.isPresent()) {
                model.addAttribute("usuario", usuarioActualizado.get());
                session.setAttribute("usuarioLogueado", usuarioActualizado.get());
            } else {
                return "redirect:/usuarios/logout";
            }
            return "perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el perfil");
            return "redirect:/usuarios/login";
        }
    }

    // Página de editar perfil
    @GetMapping("/editar")
    public String mostrarEditarPerfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        model.addAttribute("usuario", usuario);
        return "editar-perfil";
    }

    // Procesar edición de perfil
    @PostMapping("/editar")
    public String procesarEditarPerfil(@ModelAttribute Usuario usuario,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/usuarios/login";
        }

        try {
            usuario.setIdUsuario(usuarioLogueado.getIdUsuario());
            usuario.setPassword(usuarioLogueado.getPassword()); // Mantener password actual
            Usuario actualizado = usuarioService.actualizarPerfil(usuario);
            session.setAttribute("usuarioLogueado", actualizado);
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
            return "redirect:/usuarios/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al actualizar perfil");
            return "redirect:/usuarios/editar";
        }
    }

    // Página cambiar contraseña
    @GetMapping("/cambiar-password")
    public String mostrarCambiarPassword(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }
        return "cambiar-password";
    }

    // Procesar cambio de contraseña
    @PostMapping("/cambiar-password")
    public String procesarCambiarPassword(@RequestParam String passwordActual,
                                          @RequestParam String nuevaPassword,
                                          @RequestParam String confirmarPassword,
                                          HttpSession session,
                                          RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        // Validar contraseña actual
        if (!usuario.getPassword().equals(passwordActual)) {
            redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
            return "redirect:/usuarios/cambiar-password";
        }

        // Validar que las nuevas contraseñas coincidan
        if (!nuevaPassword.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/usuarios/cambiar-password";
        }

        try {
            Usuario actualizado = usuarioService.cambiarPassword(usuario.getIdUsuario(), passwordActual, nuevaPassword);
            session.setAttribute("usuarioLogueado", actualizado);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña cambiada correctamente");
            return "redirect:/usuarios/perfil";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/usuarios/cambiar-password";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al cambiar contraseña");
            return "redirect:/usuarios/cambiar-password";
        }
    }

    // Eliminar cuenta
    @PostMapping("/eliminar")
    public String eliminarCuenta(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        // No permitir que el admin se elimine a sí mismo
        if (usuario.esAdmin()) {
            redirectAttributes.addFlashAttribute("error", "Los administradores no pueden eliminar su propia cuenta por seguridad del sistema.");
            return "redirect:/usuarios/perfil";
        }

        try {
            usuarioService.deleteById(usuario.getIdUsuario());
            session.invalidate();
            redirectAttributes.addFlashAttribute("mensaje", "Tu cuenta ha sido eliminada correctamente");
            return "redirect:/usuarios/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cuenta: " + e.getMessage());
            return "redirect:/usuarios/perfil";
        }
    }

    // Listar todos los usuarios (búsqueda)
    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam(required = false) String query,
                                @RequestParam(required = false) String tipo,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/usuarios/login";
        }

        // Verificar que sea admin
        if (!usuario.esAdmin()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para buscar usuarios. Solo los administradores pueden hacerlo.");
            return "redirect:/peliculas";
        }

        List<Usuario> usuarios;

        if (query != null && !query.trim().isEmpty()) {
            if ("username".equals(tipo)) {
                usuarios = usuarioService.buscarPorUsername(query);
            } else {
                usuarios = usuarioService.buscarPorNombre(query);
            }
            model.addAttribute("query", query);
            model.addAttribute("tipo", tipo);
        } else {
            usuarios = usuarioService.findAll();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", usuario);
        return "buscar-usuarios";
    }
}
