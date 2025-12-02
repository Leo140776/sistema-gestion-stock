package com.traversol.gestion_stock.service;

import com.traversol.gestion_stock.model.Producto;
import com.traversol.gestion_stock.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository repository;

    public List<Producto> findAll() {
        return repository.findAll();
    }

    public Producto save(Producto producto) {
        producto.setStockActual(producto.getStockInicial());
        return repository.save(producto);
    }
    public List<Producto> getProductosConStockBajo() {
        return findAll().stream()
                .filter(p -> p.getStockActual() <= p.getStockMinimo())
                .collect(Collectors.toList());
    }


    public boolean existsBySku(String sku) {
        return repository.existsBySku(sku);
    }

    public Optional<Producto> findBySku(String sku) {
        return repository.findBySku(sku);
    }

    //Para incrementar y decrementar stock
    public void actualizarStock(String sku, int cantidad) {
        Optional<Producto> optionalProducto = findBySku(sku);
        if (optionalProducto.isEmpty()) {
            throw new IllegalArgumentException("Producto no encontrado con SKU: " + sku);
        }
        Producto producto = optionalProducto.get();
        int nuevoStock = producto.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("Stock no puede ser negativo");
        }
        producto.setStockActual(nuevoStock);
        repository.save(producto);
    }
}