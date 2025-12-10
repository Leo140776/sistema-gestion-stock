package com.traversol.gestion_stock.util;

import com.traversol.gestion_stock.model.Transaccion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {
    private XSSFWorkbook workbook;
    private Sheet sheet;
    private List<Transaccion> listaTransacciones;

    public ExcelExporter(List<Transaccion> listaTransacciones) {
        this.listaTransacciones = listaTransacciones;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Reporte Stock");
        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        createCell(row, 0, "ID", style);
        createCell(row, 1, "Fecha", style);
        createCell(row, 2, "Tipo", style);
        createCell(row, 3, "Producto", style);
        createCell(row, 4, "Cantidad", style);
        createCell(row, 5, "Motivo", style);
        createCell(row, 6, "Usuario", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        
        // Formato para que la fecha se vea linda en el Excel
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (Transaccion t : listaTransacciones) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, t.getId(), style);
            
            // Fecha formateada
            String fechaStr = t.getFecha() != null ? t.getFecha().format(formatter) : "";
            createCell(row, columnCount++, fechaStr, style);
            
            createCell(row, columnCount++, t.getTipo().toString(), style);

            // CORRECCIÓN: Solo mostramos el NOMBRE del producto (más limpio)
            String nombreProducto = t.getProducto() != null ? t.getProducto().getNombre() : "Eliminado";
            createCell(row, columnCount++, nombreProducto, style);
            
            createCell(row, columnCount++, t.getCantidad(), style);
            createCell(row, columnCount++, t.getMotivo(), style);
            
            // Usuario (evitando error si es nulo)
            String usuario = t.getUsuario() != null ? t.getUsuario().getEmail() : "Sistema";
            createCell(row, columnCount++, usuario, style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}