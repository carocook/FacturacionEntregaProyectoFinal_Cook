package com.coderhouse.services;

import java.util.List;

import com.coderhouse.dto.ItemFacturaDTO;
import com.coderhouse.models.FacturaProducto;
import com.coderhouse.models.FacturaProductoId;
import com.coderhouse.repositories.FacturaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderhouse.dto.FacturaRequestDTO;
import com.coderhouse.interfaces.CRUDInterface;
import com.coderhouse.models.Cliente;
import com.coderhouse.models.Factura;
import com.coderhouse.models.Producto;
import com.coderhouse.repositories.ClienteRepository;
import com.coderhouse.repositories.FacturaRepository;
import com.coderhouse.repositories.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class FacturaService implements CRUDInterface<Factura, Long> {

    private final String message = "Factura no encontrada";
    private final String messageCliente = "Cliente no encontrado";

    @Autowired
    private FacturaProductoRepository itemFacturaRepo;

    @Autowired
    private FacturaRepository repo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Override
    public List<Factura> findAll() {
        return repo.findAll();
    }

    @Override
    public Factura findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    @Override
    public Factura save(Factura facturaNueva) {
        if (facturaNueva.getNumero() != null && repo.existsByNumero(facturaNueva.getNumero())) {
            throw new IllegalStateException("Ya existe una factura con este número");
        }
        return repo.save(facturaNueva);
    }

    @Override
    @Transactional
    public Factura update(Long id, Factura facturaActualizada) {
        Factura factura = findById(id);

        if (facturaActualizada.getNumero() != null && !facturaActualizada.getNumero().isEmpty()) {
            factura.setNumero(facturaActualizada.getNumero());
        }

        if (facturaActualizada.getTotal() > 0) {
            factura.setTotal(facturaActualizada.getTotal());
        }

        if (facturaActualizada.getFecha() != null) {
            factura.setFecha(facturaActualizada.getFecha());
        }

        return repo.save(factura);
    }

    @Override
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException(message);
        }
        repo.deleteById(id);
    }

    @Transactional
    public Factura createFactura(FacturaRequestDTO dto) {
        // 1. Validar y obtener Cliente
        Cliente cliente = clienteRepo.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        if (dto.getNumero() == null || dto.getNumero().trim().isEmpty() || repo.existsByNumero(dto.getNumero())) {
            throw new IllegalStateException("Número de factura inválido o ya existe");
        }

        Factura factura = new Factura();
        factura.setNumero(dto.getNumero());
        factura.setCliente(cliente);
        factura.setTotal(0.0f);

        factura = repo.save(factura);

        float totalFactura = 0.0f;

        for (ItemFacturaDTO itemDto : dto.getItems()) {
            Producto producto = productoRepo.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + itemDto.getProductoId()));

            if (itemDto.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser un número positivo para el producto ID: " + itemDto.getProductoId());
            }

            FacturaProductoId fpId = new FacturaProductoId();
            fpId.setFacturaId(factura.getId());
            fpId.setProductoId(producto.getId());

            FacturaProducto item = new FacturaProducto();
            item.setId(fpId);
            item.setFactura(factura);
            item.setProducto(producto);
            item.setCantidad(itemDto.getCantidad());


            float subtotal = itemDto.getCantidad() * producto.getPrecio();
            item.setSubtotal(subtotal);
            factura.getItems().add(item);
            totalFactura += subtotal;
             itemFacturaRepo.save(item);
        }

        factura.setTotal(totalFactura);
        return repo.save(factura);
    }
}