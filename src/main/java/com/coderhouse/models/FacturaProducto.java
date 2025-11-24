package com.coderhouse.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "factura_producto")
public class FacturaProducto implements Serializable {

    @EmbeddedId
    private FacturaProductoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("facturaId")
    @JoinColumn(name = "factura_id")
    @JsonBackReference
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "precio_unitario", nullable = false)
    private float precioUnitario;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "subtotal", nullable = false)
    private float subtotal;
}