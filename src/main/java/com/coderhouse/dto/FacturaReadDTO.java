package com.coderhouse.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaReadDTO {
    private Long id;
    private String numero;
    private LocalDateTime fecha;
    private Long clienteId;
    private String clienteNombreCompleto;
    private float total;
}