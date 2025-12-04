package com.traversol.gestion_stock.repository;

import com.traversol.gestion_stock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // 1. Verificar si existe el SKU
    boolean existsBySku(String sku);

    // 2. Buscar el número de SKU más alto
    @Query(value = "SELECT MAX(CAST(sku AS UNSIGNED)) FROM productos", nativeQuery = true)
    Long obtenerMaxSku();

    // 3. Buscar productos con stock bajo (Stock Actual <= Stock Mínimo)
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosConStockBajo();
}