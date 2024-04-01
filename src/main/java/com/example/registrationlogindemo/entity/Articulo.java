package com.example.registrationlogindemo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
public class Articulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User user;

    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String contenido;

    private LocalDate fecha;
    private LocalTime hora;
    @Column(columnDefinition = "TEXT")
    private String imagen;
    private String categoria;
    public String getImagen(){
        return imagen;
    }
    public void setImagen(String imagen){
        this.imagen = imagen;
    }
}
