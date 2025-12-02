package com.traversol.gestion_stock.controller;

import com.traversol.gestion_stock.model.Producto;
import com.traversol.gestion_stock.model.TipoTransaccion;
import com.traversol.gestion_stock.model.Transaccion;
import com.traversol.gestion_stock.repository.ProductoRepository;
import com.traversol.gestion_stock.repository.TransaccionRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TransaccionController {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // --- MÉTODOS DE INGRESOS Y EGRESOS (IGUAL QUE ANTES) ---
    @GetMapping("/egresos/nuevo")
    public String nuevoEgreso(Model model) {
        model.addAttribute("transaccion", new Transaccion());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("titulo", "Registrar Egreso");
        model.addAttribute("esIngreso", false);
        return "form_transaccion";
    }

    @PostMapping("/egresos/guardar")
    public String guardarEgreso(@Valid @ModelAttribute("transaccion") Transaccion transaccion, 
                                BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("titulo", "Registrar Egreso");
            model.addAttribute("esIngreso", false);
            return "form_transaccion";
        }
        Producto prod = transaccion.getProducto();
        if (prod.getStockActual() < transaccion.getCantidad()) {
            model.addAttribute("error", "Error: Stock insuficiente.");
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("titulo", "Registrar Egreso");
            model.addAttribute("esIngreso", false);
            return "form_transaccion";
        }
        int nuevoStock = prod.getStockActual() - transaccion.getCantidad();
        transaccion.setTipo(TipoTransaccion.EGRESO);
        prod.setStockActual(nuevoStock);
        productoRepository.save(prod);
        transaccionRepository.save(transaccion);

        if (nuevoStock <= prod.getStockMinimo()) {
            redirectAttributes.addFlashAttribute("mensajeAlerta", "⚠️ ¡ATENCIÓN! Stock bajo para: " + prod.getNombre());
        } else {
            redirectAttributes.addFlashAttribute("mensajeExito", "Egreso registrado correctamente.");
        }
        return "redirect:/productos";
    }

    @GetMapping("/ingresos/nuevo")
    public String nuevoIngreso(Model model) {
        model.addAttribute("transaccion", new Transaccion());
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("titulo", "Registrar Ingreso");
        model.addAttribute("esIngreso", true);
        return "form_transaccion";
    }

    @PostMapping("/ingresos/guardar")
    public String guardarIngreso(@Valid @ModelAttribute("transaccion") Transaccion transaccion,
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("titulo", "Registrar Ingreso");
            model.addAttribute("esIngreso", true);
            return "form_transaccion";
        }
        transaccion.setTipo(TipoTransaccion.INGRESO);
        Producto prod = transaccion.getProducto();
        prod.setStockActual(prod.getStockActual() + transaccion.getCantidad());
        productoRepository.save(prod);
        transaccionRepository.save(transaccion);
        redirectAttributes.addFlashAttribute("mensajeExito", "Ingreso registrado correctamente.");
        return "redirect:/productos";
    }

    // --- REPORTES ---
    @GetMapping("/reportes")
    public String verReportes(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                              @RequestParam(required = false) TipoTransaccion tipo,
                              @RequestParam(required = false) String motivo,
                              @RequestParam(required = false) Integer productoId,
                              Model model) {
        
        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;
        if (motivo != null && motivo.isEmpty()) motivo = null;

        List<Transaccion> lista = transaccionRepository.buscarConFiltros(inicio, fin, tipo, motivo, productoId);

        // Seguridad Empleado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esEmpleado = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));

        if (esEmpleado) {
            lista = lista.stream().filter(t -> t.getTipo() != TipoTransaccion.EGRESO).collect(Collectors.toList());
        }

        model.addAttribute("transacciones", lista);
        // NOTA: Ya no mandamos "productos" aquí porque se cargan dinámicamente con JS, 
        // pero lo dejamos para la carga inicial "Todos"
        model.addAttribute("productos", productoRepository.findAll()); 
        return "reporte";
    }

    // --- EXCEL ---
    @GetMapping("/reportes/exportar")
    public void exportarExcel(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                              @RequestParam(required = false) TipoTransaccion tipo,
                              @RequestParam(required = false) String motivo,
                              @RequestParam(required = false) Integer productoId,
                              HttpServletResponse response) throws IOException {

        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=reporte_stock_" + LocalDate.now() + ".xlsx";
        response.setHeader(headerKey, headerValue);

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(LocalTime.MAX) : null;
        if (motivo != null && motivo.isEmpty()) motivo = null;

        List<Transaccion> lista = transaccionRepository.buscarConFiltros(inicio, fin, tipo, motivo, productoId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esEmpleado = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_EMPLEADO"));

        if (esEmpleado) {
            lista = lista.stream().filter(t -> t.getTipo() != TipoTransaccion.EGRESO).collect(Collectors.toList());
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Movimientos");
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Fecha", "Tipo", "Producto", "Cantidad", "Motivo", "Observaciones"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Transaccion t : lista) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(t.getId());
            row.createCell(1).setCellValue(t.getFecha().format(formatter));
            row.createCell(2).setCellValue(t.getTipo().toString());
            row.createCell(3).setCellValue(t.getProducto().getNombre());
            row.createCell(4).setCellValue(t.getCantidad());
            row.createCell(5).setCellValue(t.getMotivo());
            row.createCell(6).setCellValue(t.getObservaciones() != null ? t.getObservaciones() : "");
        }
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // --- NUEVA API PARA FILTRO DINÁMICO ---
    @GetMapping("/api/productos-filtrados")
    @ResponseBody
    public List<Producto> obtenerProductosFiltrados(@RequestParam(required = false) TipoTransaccion tipo,
                                                    @RequestParam(required = false) String motivo) {
        if (motivo != null && motivo.isEmpty()) motivo = null;
        
        // Busca usando el método nuevo del repositorio
        return transaccionRepository.findProductosConTransacciones(tipo, motivo);
    }
}