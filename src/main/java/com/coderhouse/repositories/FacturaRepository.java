package com.coderhouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coderhouse.models.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    boolean existsByNumero(String numero);
}
