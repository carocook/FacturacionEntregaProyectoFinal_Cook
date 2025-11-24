package com.coderhouse.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaResponseDTO {
    private Long facturaId;
    private String numero;
    private LocalDateTime fecha;
    private Long clienteId;
    private float totalVenta;
    private int cantidadProductosVendidos;
    private String mensaje;
}