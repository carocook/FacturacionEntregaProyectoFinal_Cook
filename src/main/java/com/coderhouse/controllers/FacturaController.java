package com.coderhouse.controllers;

import java.util.List;

import com.coderhouse.dto.FacturaReadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.*;

import com.coderhouse.dto.FacturaRequestDTO;
import com.coderhouse.dto.FacturaResponseDTO;
import com.coderhouse.models.Factura;
import com.coderhouse.services.FacturaService;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService svc;

    @GetMapping
    public ResponseEntity<List<FacturaReadDTO>> getAllFacturas() {
        try {
            return ResponseEntity.ok(svc.findAllFacturasDTO());
        } catch (Exception e) {
            System.err.println("Error al listar facturas: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

  @PostMapping("/create")
    public ResponseEntity<FacturaResponseDTO> createFactura(
            @RequestBody FacturaRequestDTO facturaDTO) {

        try {
            FacturaResponseDTO responseDTO = svc.createFactura(facturaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (IllegalArgumentException e) {
            FacturaResponseDTO errorResponse = new FacturaResponseDTO();
            errorResponse.setMensaje("ERROR - Dato no encontrado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (IllegalStateException e) {
            FacturaResponseDTO errorResponse = new FacturaResponseDTO();
            errorResponse.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (Exception e) {
            FacturaResponseDTO errorResponse = new FacturaResponseDTO();
            errorResponse.setMensaje("ERROR INTERNO DEL SERVIDOR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{facturaId}")
    public ResponseEntity<Factura> updateFacturaById(
            @PathVariable Long facturaId,
            @RequestBody Factura facturaActualizada) {
        try {
            return ResponseEntity.ok(svc.update(facturaId, facturaActualizada));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{facturaId}")
    public ResponseEntity<Void> deleteFacturaById(@PathVariable Long facturaId) {
        try {
            svc.deleteById(facturaId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
