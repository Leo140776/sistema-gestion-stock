package com.traversol.gestion_stock.service;

import com.traversol.gestion_stock.model.Transaccion;
import com.traversol.gestion_stock.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService{

    @Autowired
    private TransaccionRepository transaccionRepository;

    public Map<String, Object> generarReporteSemanal() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioSemana = ahora.minus(7, ChronoUnit.DAYS)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime finSemana = ahora.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<Transaccion> transacciones = transaccionRepository.findByFechaBetween(inicioSemana, finSemana);

        Map<String, Integer> totales = calcularTotales(transacciones);

        Map<String, Object> reporte = new HashMap<>();
        reporte.put("transacciones", transacciones);
        reporte.put("totales", totales);
        reporte.put("inicio", inicioSemana);
        reporte.put("fin", finSemana);

        return reporte;
    }
    private Map<String, Integer> calcularTotales(List<Transaccion> transacciones) {
        Map<String, Integer> totales = new HashMap<>();
        totales.put("INGRESO", 0);
        totales.put("EGRESO", 0);
        totales.put("DESPERDICIO", 0);

        for (Transaccion t : transacciones) {
            String tipoStr = t.getTipo().name();
            totales.put(tipoStr, totales.getOrDefault(tipoStr, 0) + t.getCantidad());
        }

        return totales;
    }
}