package com.coderhouse.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Descripcion", nullable = false)
    private String descripcion;

    @Column(name = "Codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "Stock", nullable = false)
    private int stock;

    @Column(name = "Precio", nullable = false)
    private float precio;

    @OneToMany(mappedBy = "producto")
    @JsonIgnore
    private List<FacturaProducto> itemsFactura = new ArrayList<>();

}
