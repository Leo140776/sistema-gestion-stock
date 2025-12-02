package com.traversol.gestion_stock.model;

public class DesperdicioSemanal {
    private String sku;
    private int semana;
    private long totalPerdido;

    public DesperdicioSemanal(String sku, int semana, long totalPerdido) {
        this.sku = sku;
        this.semana = semana;
        this.totalPerdido = totalPerdido;
    }

    // Getters (necesarios para Thymeleaf en reportes.html)
    public String getSku() {
        return sku;
    }

    public int getSemana() {
        return semana;
    }

    public long getTotalPerdido() {
        return totalPerdido;
    }
}