package com.coderhouse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderhouse.interfaces.CRUDInterface;
import com.coderhouse.models.Producto;
import com.coderhouse.repositories.ProductoRepository;
import lombok.*;

import jakarta.transaction.Transactional;

@Service
public class ProductoService implements CRUDInterface<Producto, Long> {

    private final String message = "Producto no encontrado";

    @Autowired
    private ProductoRepository repo;

    @Override
    public List<Producto> findAll() {
        return repo.findAll();
    }

    @Override
    public Producto findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    @Override
    @Transactional
    public Producto save(Producto productoNuevo) {
        if (productoNuevo.getDescripcion() != null && repo.existsByDescripcion(productoNuevo.getDescripcion())) {
            throw new IllegalStateException("El producto con esta descripción ya existe");
        }
        return repo.save(productoNuevo);
    }

    @Override
    @Transactional
    public Producto update(Long id, Producto productoActualizado) {
        Producto producto = findById(id);

        if (productoActualizado.getDescripcion() != null && !productoActualizado.getDescripcion().isEmpty()) {
            producto.setDescripcion(productoActualizado.getDescripcion());
        }

        producto.setPrecio(productoActualizado.getPrecio());
        producto.setStock(productoActualizado.getStock());

        return repo.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException(message);
        }
        repo.deleteById(id);
    }
}
