package com.coderhouse.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coderhouse.models.FacturaProducto;
import com.coderhouse.models.FacturaProductoId;

public interface FacturaProductoRepository extends JpaRepository<FacturaProducto, FacturaProductoId> {

}