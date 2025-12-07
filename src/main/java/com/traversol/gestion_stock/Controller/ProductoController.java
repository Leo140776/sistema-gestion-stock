package com.traversol.gestion_stock.controller;

import com.traversol.gestion_stock.model.Producto;
import com.traversol.gestion_stock.repository.ProductoRepository;
import com.traversol.gestion_stock.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String listarProductos(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "producto_list"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeRegistrarProducto(Model model) {
        Producto producto = new Producto();
        model.addAttribute("producto", producto);
        return "producto_form"; 
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, Model model) {
        
        // --- VALIDACIÓN 1: SKU NEGATIVO ---
        if (producto.getSku() != null && producto.getSku().trim().startsWith("-")) {
            model.addAttribute("error", "¡Error! El SKU no puede ser negativo.");
            model.addAttribute("producto", producto);
            return "producto_form";
        }

        // --- VALIDACIÓN 2: SKU DUPLICADO (Lógica Avanzada para Edición) ---
        
        // Buscamos si existe algún producto en la BD con ese mismo SKU
        Optional<Producto> existente = productoRepository.findBySku(producto.getSku());

        boolean esDuplicado = false;

        if (existente.isPresent()) {
            // Si el ID del producto que intentamos guardar es NULL (es nuevo)
            // O si el ID es distinto al que encontramos en la base de datos...
            if (producto.getId() == null || !existente.get().getId().equals(producto.getId())) {
                esDuplicado = true; // ...entonces sí es un duplicado ilegal.
            }
        }

        if (esDuplicado) {
            // Calculamos sugerencia
            Long maxSku = productoRepository.obtenerMaxSku();
            Long siguienteSku = (maxSku != null) ? (maxSku + 1) : 1;

            model.addAttribute("error", "¡Atención! El SKU " + producto.getSku() + " ya existe en otro producto.");
            
            // Solo sugerimos si el SKU es numérico
            if (producto.getSku().matches("\\d+")) {
                 model.addAttribute("sugerencia", "Disponible: " + siguienteSku);
            }
            
            model.addAttribute("producto", producto);
            return "producto_form"; 
        }
        // ------------------------------------

        // Si pasó las validaciones, guardamos
        productoService.guardarProducto(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerProductoPorId(id));
        return "producto_form"; 
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/productos";
    }
}