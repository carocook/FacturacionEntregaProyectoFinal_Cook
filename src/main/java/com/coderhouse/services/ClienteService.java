package com.coderhouse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderhouse.interfaces.CRUDInterface;
import com.coderhouse.models.Cliente;
import com.coderhouse.models.Producto;
import com.coderhouse.repositories.ClienteRepository;
import com.coderhouse.repositories.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class ClienteService implements CRUDInterface<Cliente, Long> {

    private final String message = "Cliente no encontrado";

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private ProductoRepository productoRepo;

    @Override
    public List<Cliente> findAll() {
        return repo.findAll();
    }

    @Override
    public Cliente findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(message));
    }

    @Override
    @Transactional
    public Cliente save(Cliente nuevoCliente) {
        if (nuevoCliente.getDni() != 0 && repo.existsByDni(nuevoCliente.getDni())) {
            throw new IllegalStateException("Este DNI ya existe");
        }
        return repo.save(nuevoCliente);
    }

    @Override
    @Transactional
    public Cliente update(Long id, Cliente clienteActualizado) {
        Cliente cliente = findById(id);

        if (clienteActualizado.getNombre() != null && !clienteActualizado.getNombre().isEmpty()) {
            cliente.setNombre(clienteActualizado.getNombre());
        }

        if (clienteActualizado.getApellido() != null && !clienteActualizado.getApellido().isEmpty()) {
            cliente.setApellido(clienteActualizado.getApellido());
        }

        if (clienteActualizado.getDni() != 0) {
            cliente.setDni(clienteActualizado.getDni());
        }

        return repo.save(cliente);
    }

    @Override
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException(message);
        }
        repo.deleteById(id);
    }
}
