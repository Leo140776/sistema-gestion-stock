package com.traversol.gestion_stock.repository;

import com.traversol.gestion_stock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional; // Importación necesaria

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // 1. Verifica si existe (Devuelve true/false)
    boolean existsBySku(String sku);

    // 2. Busca el objeto producto completo por SKU (Necesario para validar ediciones)
    Optional<Producto> findBySku(String sku);

    // 3. Buscar el número de SKU más alto para sugerencias
    @Query(value = "SELECT MAX(CAST(sku AS UNSIGNED)) FROM productos", nativeQuery = true)
    Long obtenerMaxSku();

    // 4. Buscar productos con stock bajo
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosConStockBajo();
}