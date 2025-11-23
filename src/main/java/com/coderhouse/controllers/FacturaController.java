package com.coderhouse.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.coderhouse.dto.FacturaRequestDTO;
import com.coderhouse.models.Factura;
import com.coderhouse.responses.ErrorResponse;
import com.coderhouse.services.FacturaService;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService svc;

    @GetMapping
    public ResponseEntity<List<Factura>> getAllFacturas() {
        try {
            return ResponseEntity.ok(svc.findAll());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{facturaId}")
    public ResponseEntity<Factura> getFacturasById(@PathVariable Long facturaId) {
        try {
            return ResponseEntity.ok(svc.findById(facturaId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFactura(@RequestBody FacturaRequestDTO facturaDTO) {
        try {
            Factura facturaCreada = svc.createFactura(facturaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(facturaCreada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Datos inválidos", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Conflicto", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Error interno", e.getMessage()));
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
