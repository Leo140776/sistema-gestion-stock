package com.traversol.gestion_stock.controller;

import com.traversol.gestion_stock.model.Producto;
import com.traversol.gestion_stock.repository.ProductoRepository;
import com.traversol.gestion_stock.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        // CORRECCIÓN AQUÍ: Antes decía "productos", ahora apunta al archivo real "producto_list"
        return "producto_list"; 
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioDeRegistrarProducto(Model model) {
        Producto producto = new Producto();
        model.addAttribute("producto", producto);
        return "producto_form"; // Apunta a producto_form.html
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, Model model) {
        
        // --- VALIDACIÓN DE SKU DUPLICADO ---
        // Si es nuevo (ID nulo) y el SKU ya existe...
        if (producto.getId() == null && productoRepository.existsBySku(producto.getSku())) {
            
            Long maxSku = productoRepository.obtenerMaxSku();
            Long siguienteSku = (maxSku != null) ? (maxSku + 1) : 1;

            model.addAttribute("error", "¡Atención! El SKU " + producto.getSku() + " ya existe.");
            model.addAttribute("sugerencia", "Disponible: " + siguienteSku);
            
            // Devolvemos lo que escribió el usuario para que no se borre
            model.addAttribute("producto", producto);
            
            // Volvemos al formulario para que corrija
            return "producto_form"; 
        }
        // ------------------------------------

        productoService.guardarProducto(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.obtenerProductoPorId(id));
        return "producto_form"; // Reusamos el mismo formulario
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/productos";
    }
}