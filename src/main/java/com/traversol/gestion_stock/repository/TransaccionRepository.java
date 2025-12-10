package com.traversol.gestion_stock.repository;

import com.traversol.gestion_stock.model.Producto;
import com.traversol.gestion_stock.model.TipoTransaccion;
import com.traversol.gestion_stock.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    // 1. Método principal para filtros (Tabla y Excel)
    @Query("SELECT t FROM Transaccion t WHERE " +
           "(:inicio IS NULL OR t.fecha >= :inicio) AND " +
           "(:fin IS NULL OR t.fecha <= :fin) AND " +
           "(:tipo IS NULL OR t.tipo = :tipo) AND " +
           "(:motivo IS NULL OR :motivo = '' OR t.motivo LIKE %:motivo%) AND " +
           "(:productoId IS NULL OR t.producto.id = :productoId)")
    List<Transaccion> buscarConFiltros(@Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin,
                                       @Param("tipo") TipoTransaccion tipo,
                                       @Param("motivo") String motivo,
                                       @Param("productoId") Integer productoId);

    // 2. Método para el desplegable de productos
    @Query("SELECT DISTINCT t.producto FROM Transaccion t WHERE " +
           "(:tipo IS NULL OR t.tipo = :tipo) AND " +
           "(:motivo IS NULL OR :motivo = '' OR t.motivo LIKE %:motivo%)")
    List<Producto> findProductosConTransacciones(@Param("tipo") TipoTransaccion tipo,
                                                 @Param("motivo") String motivo);

    // 3. ¡EL MÉTODO QUE FALTABA! (Para solucionar el error de compilación)
    // Busca egresos que mencionen "rotura" o "desperdicio" en el motivo
    @Query("SELECT t FROM Transaccion t WHERE t.fecha BETWEEN :inicio AND :fin " +
           "AND t.tipo = 'EGRESO' " +
           "AND (LOWER(t.motivo) LIKE '%rotura%' OR LOWER(t.motivo) LIKE '%desperdicio%')")
    List<Transaccion> getDesperdicioSemanal(@Param("inicio") LocalDateTime inicio,
                                            @Param("fin") LocalDateTime fin);
}