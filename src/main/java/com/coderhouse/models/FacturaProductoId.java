package com.coderhouse.models;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class FacturaProductoId implements Serializable {
    private Long facturaId;
    private Long productoId;
}