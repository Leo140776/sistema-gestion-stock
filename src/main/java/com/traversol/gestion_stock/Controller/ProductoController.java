package com.traversol.gestion_stock.controller;

import com.traversol.gestion_stock.model.Producto;
import com.traversol.gestion_stock.repository.ProductoRepository; // Ajusta si usas Service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository; // O tu ProductoService

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        return "producto_list"; // Apunta al HTML producto_list.html
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto_form"; // Apunta al HTML producto_form.html
    }

    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto, 
                                  BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "producto_form"; // Si hay errores, vuelve al formulario mostrando las alertas
        }
        
        // Si es nuevo (id nulo), inicializa stocks si vienen nulos (opcional, por seguridad)
        if (producto.getId() == null) {
            if(producto.getStockActual() == null) producto.setStockActual(0);
        }

        productoRepository.save(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        model.addAttribute("producto", producto);
        return "producto_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id) {
        productoRepository.deleteById(id);
        return "redirect:/productos";
    }
}