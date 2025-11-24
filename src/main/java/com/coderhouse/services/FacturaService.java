package com.coderhouse.services;

import java.time.LocalDateTime;
import java.util.List;

import com.coderhouse.dto.*;
import com.coderhouse.models.FacturaProducto;
import com.coderhouse.models.FacturaProductoId;
import com.coderhouse.repositories.FacturaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private FacturaProductoRepository itemFacturaRepo;

    @Autowired
    private FacturaRepository repo;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private RestTemplate restTemplate;

    public List<FacturaReadDTO> findAllFacturasDTO() {
        return repo.findAll().stream()
                .map(factura -> {
                    FacturaReadDTO dto = new FacturaReadDTO();
                    dto.setId(factura.getId());
                    dto.setNumero(factura.getNumero());
                    dto.setFecha(factura.getFecha());
                    dto.setTotal(factura.getTotal());

                    if (factura.getCliente() != null) {
                        dto.setClienteId(factura.getCliente().getId());
                        dto.setClienteNombreCompleto(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
                    }

                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }


    @Override
    public List<Factura> findAll() {
        return List.of();
    }

    @Override
    public Factura findById(Long aLong) {
        return null;
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
    public FacturaResponseDTO createFactura(FacturaRequestDTO dto) {

        Long clienteId = dto.getCliente().getClienteid();
        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setTotal(0.0f);

        String numeroTemporal = "TEMP-" + clienteId + "-" + System.currentTimeMillis();
        factura.setNumero(numeroTemporal);

        factura = repo.save(factura);

        float totalFactura = 0.0f;
        StringBuilder errores = new StringBuilder();

        for (ItemFacturaDTO itemDto : dto.getLineas()) {
            Long productoId = itemDto.getProducto().getProductoid();
            int cantidadSolicitada = itemDto.getCantidad();

            Producto producto = productoRepo.findById(productoId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + productoId));

            if (cantidadSolicitada <= 0) {
                errores.append("La cantidad debe ser positiva para el producto ID: ").append(productoId).append(". ");
                continue;
            }

            if (cantidadSolicitada > producto.getStock()) {
                errores.append("Stock insuficiente para el producto '").append(producto.getDescripcion())
                        .append("'. Solicitado: ").append(cantidadSolicitada)
                        .append(", Stock actual: ").append(producto.getStock()).append(". ");
                continue;
            }

            FacturaProductoId fpId = new FacturaProductoId();
            fpId.setFacturaId(factura.getId());
            fpId.setProductoId(producto.getId());

            FacturaProducto item = new FacturaProducto();
            item.setId(fpId);
            item.setFactura(factura);
            item.setProducto(producto);
            item.setCantidad(cantidadSolicitada);

            item.setPrecioUnitario(producto.getPrecio());

            float subtotal = cantidadSolicitada * producto.getPrecio();
            item.setSubtotal(subtotal);
            factura.getItems().add(item);
            totalFactura += subtotal;
            itemFacturaRepo.save(item);

            producto.setStock(producto.getStock() - cantidadSolicitada);
            productoRepo.save(producto);
        }

        if (errores.length() > 0) {
            repo.delete(factura);
            throw new IllegalStateException("Error de validación de la factura: " + errores.toString());
        }

        factura.setFecha(getFechaComprobante());

        factura.setTotal(totalFactura);
        factura = repo.save(factura);

        int cantidadTotalVendida = dto.getLineas().stream()
                .mapToInt(ItemFacturaDTO::getCantidad)
                .sum();

        FacturaResponseDTO responseDTO = new FacturaResponseDTO();
        responseDTO.setFacturaId(factura.getId());
        responseDTO.setNumero(factura.getNumero());
        responseDTO.setFecha(factura.getFecha());
        responseDTO.setClienteId(factura.getCliente().getId());
        responseDTO.setTotalVenta(factura.getTotal());
        responseDTO.setCantidadProductosVendidos(cantidadTotalVendida);
        responseDTO.setMensaje("Factura creada exitosamente. Stock actualizado.");

        return responseDTO;
    }

    private LocalDateTime getFechaComprobante() {
        final String API_URL = "http://worldclockapi.com/api/json/utc/now";

        try {
            WorldClockDTO response = restTemplate.getForObject(API_URL, WorldClockDTO.class);

            if (response != null && response.getCurrentDateTime() != null) {
                String dateString = response.getCurrentDateTime();

                return LocalDateTime.parse(dateString.replace("Z", ""));
            }

        } catch (ResourceAccessException e) {
            System.err.println("Error de conexión al servicio WorldClock. Usando fecha local. Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Fallo al obtener la fecha de la API externa. Usando fecha local. Error: " + e.getMessage());
        }

        return LocalDateTime.now();
    }
}