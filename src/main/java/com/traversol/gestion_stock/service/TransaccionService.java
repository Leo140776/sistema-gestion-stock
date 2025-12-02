package com.traversol.gestion_stock.service;

import com.traversol.gestion_stock.model.Transaccion;
import com.traversol.gestion_stock.repository.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    public void guardar(Transaccion transaccion) {
        transaccionRepository.save(transaccion);
    }

    public List<Transaccion> listarTodas() {
        return transaccionRepository.findAll();
    }

    // CORRECCIÓN: Ahora devuelve List<Transaccion> en lugar de List<Object[]>
    public List<Transaccion> getDesperdicioSemanal(LocalDateTime inicio, LocalDateTime fin) {
        return transaccionRepository.getDesperdicioSemanal(inicio, fin);
    }
}