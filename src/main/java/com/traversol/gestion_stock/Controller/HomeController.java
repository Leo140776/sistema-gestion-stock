package com.traversol.gestion_stock.Controller;

import com.traversol.gestion_stock.model.Usuario;
import com.traversol.gestion_stock.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private ProductoService productoService;

    @GetMapping({"/", "/home"})
    public String home(@AuthenticationPrincipal Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("productosBajos", productoService.getProductosConStockBajo());  // Alertas RF4 para todos los roles
        return "home";
    }
}