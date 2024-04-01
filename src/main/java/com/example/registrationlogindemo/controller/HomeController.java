package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Articulo;
import com.example.registrationlogindemo.entity.Comentario;
import com.example.registrationlogindemo.entity.User;
import com.example.registrationlogindemo.service.ArticuloService;
import com.example.registrationlogindemo.service.ComentarioService;
import com.example.registrationlogindemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    ArticuloService articuloService;
    @Autowired
    ComentarioService comentarioService;
    @Autowired
    UserService userService;

// Otras importaciones necesarias

    @GetMapping({"/", "/index"})
    public String index(Model model, Principal principal) {
        model.addAttribute("listaArticulos", articuloService.findAll());

        // Verifica si el usuario ha iniciado sesión
        boolean isAuthenticated = principal != null;

        // Agrega una bandera al modelo para indicar si el usuario ha iniciado sesión
        model.addAttribute("isAuthenticated", isAuthenticated);

        // Si el usuario está autenticado, agregar el usuario autenticado al modelo
        if (isAuthenticated) {
            String username = principal.getName();
            User usuarioAutenticado = userService.findByEmail(username);
            model.addAttribute("usuarioAutenticado", usuarioAutenticado);
        }

        return "index";
    }


    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable long id, Model model, Principal principal) {
        //Header
        // Verifica si el usuario ha iniciado sesión
        boolean isAuthenticated = principal != null;

        // Agrega una bandera al modelo para indicar si el usuario ha iniciado sesión
        model.addAttribute("isAuthenticated", isAuthenticated);

        // Si el usuario está autenticado, agregar el usuario autenticado al modelo
        if (isAuthenticated) {
            String username = principal.getName();
            User usuarioAutenticado = userService.findByEmail(username);
            model.addAttribute("usuarioAutenticado", usuarioAutenticado);
        }
        //Cierra Header

        // Obtén el artículo por su ID
        Articulo articulo = articuloService.findById(id);
        // Obtiene la lista de comentarios asociados al artículo (Necesario para Mostrar Comentarios)
        List<Comentario> listadoComentario = comentarioService.findByArticulo(articulo);


        // Crear una lista para almacenar los nombres de usuario
        List<String> nombresUsuarios = new ArrayList<>();
        // Para cada comentario, cargar el nombre del usuario que lo creó y agregarlo a la lista
        for (Comentario comentario : listadoComentario) {
            String nombreUsuario = comentario.getAutor().getName(); // Obtener el nombre del usuario asociado al comentario
            nombresUsuarios.add(nombreUsuario); // Agregar el nombre del usuario a la lista
        }
        // Agregar la lista de nombres de usuario al modelo
        model.addAttribute("nombresUsuarios", nombresUsuarios);


        // Verifica si el usuario está autenticado
        if (principal != null) {
            // Obtén el nombre de usuario del usuario autenticado
            String username = principal.getName();
            // Encuentra al usuario autenticado por su email (o id, según lo que estés utilizando para autenticar al usuario)
            User usuarioAutenticado = userService.findByEmail(username); // O userService.findById(id) si estás usando el id para autenticar
            // Agrega el usuario autenticado al modelo
            model.addAttribute("usuarioAutenticado", usuarioAutenticado);
        }

        // Agrega el artículo y la lista de comentarios al modelo
        model.addAttribute("articulo", articulo);
        model.addAttribute("listadoComentario", listadoComentario);

        // Agrega un nuevo comentario al modelo
        model.addAttribute("comentario", new Comentario());

        // Devuelve la vista "detalle"
        return "detalle";
    }

    @PostMapping("/detalle/submit")
    public String guardarComentario(@ModelAttribute("comentario") Comentario comentario, @RequestParam("idArticulo") Long idArticulo, @RequestParam("autorId") Long autorId) {
        // Establecer el artículo del comentario
        Articulo articulo = articuloService.findById(idArticulo);
        comentario.setArticulo(articulo);

        // Establecer el autor del comentario si es necesario
        User autor = userService.findById(autorId);
        comentario.setAutor(autor);

        // Guardar el comentario
        comentarioService.save(comentario);

        // Redirigir a la página de detalle del artículo
        return "redirect:/detalle/" + idArticulo;
    }
}
