package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Comentario;
import com.example.registrationlogindemo.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Import correcto para Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // Asegúrate de anotar como @Controller
public class ComentarioCrudController {
    @Autowired
    ComentarioService comentarioService;

    // Mostrar todos los comentarios
    @GetMapping("crud/comentarios") // Añade la ruta completa
    public String listComentarios(Model model) {
        List<Comentario> comentarios = comentarioService.findAll();
        model.addAttribute("comentarios", comentarios);

        return "listado-comentarios";
    }

    @PostMapping("/crud/comentarios/delete/{id}")
    public String deleteComentario(@PathVariable("id") long id) {
        comentarioService.deleteById(id);
        return "redirect:/crud/comentarios";
    }

}
