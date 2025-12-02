package com.traversol.gestion_stock.repository;

import com.traversol.gestion_stock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    boolean existsBySku(String sku);
    Optional<Producto> findBySku(String sku);
}