package com.traversol.gestion_stock.Controller;

import com.traversol.gestion_stock.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/reporte/semanal")
    public String reporteSemanal(Model model) {
        model.addAttribute("reporte", reporteService.generarReporteSemanal());
        return "reporte";
    }
}