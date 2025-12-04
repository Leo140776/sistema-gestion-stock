package com.traversol.gestion_stock.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "medida_tamano")
    private String medidaTamano;

    // Columna para el stock inicial (lo que ingresas en el formulario)
    @Column(name = "stock_inicial")
    private Integer stockInicial;

    // Columna para el stock actual (se debería mantener igual al inicial al crear)
    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    public Producto() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMedidaTamano() { return medidaTamano; }
    public void setMedidaTamano(String medidaTamano) { this.medidaTamano = medidaTamano; }

    public Integer getStockInicial() { return stockInicial; }
    public void setStockInicial(Integer stockInicial) { 
        this.stockInicial = stockInicial;
        // Truco: Al setear el inicial, seteamos también el actual para que no sea nulo
        if (this.stockActual == null) {
            this.stockActual = stockInicial;
        }
    }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
}