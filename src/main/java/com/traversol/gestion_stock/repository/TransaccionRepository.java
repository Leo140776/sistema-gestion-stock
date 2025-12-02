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
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    // Método 1: Para búsqueda general con filtros (Reporte)
    @Query("SELECT t FROM Transaccion t WHERE " +
           "(:inicio IS NULL OR t.fecha >= :inicio) AND " +
           "(:fin IS NULL OR t.fecha <= :fin) AND " +
           "(:tipo IS NULL OR t.tipo = :tipo) AND " +
           "(:motivo IS NULL OR t.motivo = :motivo) AND " +
           "(:productoId IS NULL OR t.producto.id = :productoId)")
    List<Transaccion> buscarConFiltros(@Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin,
                                       @Param("tipo") TipoTransaccion tipo,
                                       @Param("motivo") String motivo,
                                       @Param("productoId") Integer productoId);

    // Método 2: NUEVO - Para llenar el desplegable de productos dinámicamente
    @Query("SELECT DISTINCT t.producto FROM Transaccion t WHERE " +
           "(:tipo IS NULL OR t.tipo = :tipo) AND " +
           "(:motivo IS NULL OR t.motivo = :motivo)")
    List<Producto> findProductosConTransacciones(@Param("tipo") TipoTransaccion tipo, 
                                                 @Param("motivo") String motivo);
                                                 
    // Métodos viejos para compatibilidad (si los usas en otros lados)
    List<Transaccion> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT t FROM Transaccion t WHERE t.tipo = 'DESPERDICIO' AND t.fecha BETWEEN :inicio AND :fin")
    List<Transaccion> getDesperdicioSemanal(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}