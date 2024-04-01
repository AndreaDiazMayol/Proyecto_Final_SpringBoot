package com.example.registrationlogindemo.controller;

import com.example.registrationlogindemo.entity.Articulo;
import com.example.registrationlogindemo.entity.Comentario;
import com.example.registrationlogindemo.service.ArticuloService;
import com.example.registrationlogindemo.service.ComentarioService;
import com.example.registrationlogindemo.service.UserService;
import com.example.registrationlogindemo.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/crud/articulos")
public class ArticuloCrudController {
    @Autowired
    ArticuloService articuloService;
    @Autowired
    UserService userService;
    @Autowired
    StorageService storageService;
    @Autowired
    ComentarioService comentarioService;
    @GetMapping
    public String mostrarListadoArticulos(Model model){
        model.addAttribute("listaArticulos", articuloService.findAll());

        return "listado-articulos";
    }

    @GetMapping("/altas")
    public  String mostrarFormularioAltas(Model model, Authentication authentication){
        Articulo articulo = new Articulo();
        articulo.setUser(userService.findByEmail(authentication.getName()));
        model.addAttribute("articulo", articulo);
        return "formulario-articulos";
    }

    @PostMapping("/altas/submit")
    public String guardarDatosFormulario(@ModelAttribute Articulo articulo, @RequestParam("file") MultipartFile file,Authentication authentication){
        articulo.setUser(userService.findByEmail(authentication.getName()));
        System.out.println(file.getOriginalFilename());
        articuloService.save(articulo);
        if(!file.isEmpty()){
        String imagen = storageService.store(file, String.valueOf(articulo.getId()));
        System.out.println("La img a guardar es: "+ imagen);
        articulo.setImagen(MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "serveFile", imagen).build().toUriString());
    }
    articuloService.save(articulo);
        return "redirect:/crud/articulos/altas";
    }

    @GetMapping("/modificar/{id}")
    public String modificarArticulos(@PathVariable("id") long id, Model model){
        model.addAttribute("articulo", articuloService.findById(id));
        return "formulario-articulos";
    }

  /*@PostMapping("/modificar/submit")
    public String guardarModificaciones(@ModelAttribute Articulo articulo, Authentication authentication){
        articulo.setUser(userService.findByEmail(authentication.getName()));
        articuloService.save(articulo);
        return "redirect:/crud/articulos";
    }*/
  @PostMapping("/modificar/submit")
  public String guardarModificaciones(@ModelAttribute Articulo articulo, Authentication authentication, @RequestParam("file") MultipartFile file){
      if (!file.isEmpty()) {
          String imagen = storageService.store(file, String.valueOf(articulo.getId()));
          System.out.println("La imagen a guardar es : " + imagen);
          articulo.setImagen(MvcUriComponentsBuilder
                  .fromMethodName(FileUploadController.class, "serveFile", imagen).build().toUriString());
      }
      articulo.setUser(userService.findByEmail(authentication.getName()));
      articuloService.save(articulo);
      return "redirect:/crud/articulos";
  }

    @GetMapping("/eliminar/{id}")
    public String eliminarArticulos(@PathVariable("id") long id){
        Articulo articulo = articuloService.findById(id);
        // Obtener todos los comentarios asociados al artículo
        List<Comentario> comentarios = comentarioService.findByArticulo(articulo);

        // Eliminar los comentarios uno por uno
        for (Comentario comentario : comentarios) {
            comentarioService.deleteById(comentario.getId());
        }
        articuloService.deleteById(id);
        return "redirect:/crud/articulos";
    }
}