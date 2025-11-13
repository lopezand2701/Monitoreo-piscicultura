package com.mycompany.piscicultura_proyect.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.mycompany.piscicultura_proyect.ConexionPostgres;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteUtil {

    private static class RowDto {
        int sensorId; int estanqueId; Timestamp fecha; double valor; String tipo; String unidad;
    }

    private static List<RowDto> consultar(LocalDate ini, LocalDate fin, int estanqueId, Integer sensorId) throws Exception {
        String base = """
            SELECT m.sensor_id, m.estanque_id, m.fecha_hora, m.valor, s.tipo, s.unidad
            FROM mediciones m JOIN sensores s ON s.sensor_id = m.sensor_id
            WHERE m.estanque_id=? AND m.fecha_hora::date BETWEEN ? AND ?
            """;
        String sql = sensorId == null ? base : base + " AND m.sensor_id=?";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ps.setDate(2, java.sql.Date.valueOf(ini));
            ps.setDate(3, java.sql.Date.valueOf(fin));
            if (sensorId != null) ps.setInt(4, sensorId);
            try (ResultSet rs = ps.executeQuery()) {
                List<RowDto> rows = new ArrayList<>();
                while (rs.next()) {
                    RowDto r = new RowDto();
                    r.sensorId = rs.getInt(1);
                    r.estanqueId = rs.getInt(2);
                    r.fecha = rs.getTimestamp(3);
                    r.valor = rs.getDouble(4);
                    r.tipo = rs.getString(5);
                    r.unidad = rs.getString(6);
                    rows.add(r);
                }
                return rows;
            }
        }
    }

    public static File generarExcel(LocalDate ini, LocalDate fin, int estanqueId, Integer sensorId, File destino) throws Exception {
        List<RowDto> data = consultar(ini, fin, estanqueId, sensorId);
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sh = wb.createSheet("Mediciones");
            int r = 0;
            Row h = sh.createRow(r++);
            h.createCell(0).setCellValue("Sensor");
            h.createCell(1).setCellValue("Estanque");
            h.createCell(2).setCellValue("Fecha/Hora");
            h.createCell(3).setCellValue("Valor");
            h.createCell(4).setCellValue("Tipo");
            h.createCell(5).setCellValue("Unidad");
            for (RowDto d : data) {
                Row row = sh.createRow(r++);
                row.createCell(0).setCellValue(d.sensorId);
                row.createCell(1).setCellValue(d.estanqueId);
                row.createCell(2).setCellValue(d.fecha.toString());
                row.createCell(3).setCellValue(d.valor);
                row.createCell(4).setCellValue(d.tipo);
                row.createCell(5).setCellValue(d.unidad);
            }
            try (FileOutputStream out = new FileOutputStream(destino)) {
                wb.write(out);
            }
        }
        return destino;
    }

    public static File generarPDF(LocalDate ini, LocalDate fin, int estanqueId, Integer sensorId, File destino) throws Exception {
        List<RowDto> data = consultar(ini, fin, estanqueId, sensorId);
        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(destino));
        doc.open();
        doc.add(new Paragraph("Reporte de Mediciones - Estanque " + estanqueId));
        doc.add(new Paragraph("Rango: " + ini + " a " + fin));
        doc.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.addCell("Sensor");
        table.addCell("Estanque");
        table.addCell("Fecha/Hora");
        table.addCell("Valor");
        table.addCell("Tipo");
        table.addCell("Unidad");

        for (RowDto d : data) {
            table.addCell(String.valueOf(d.sensorId));
            table.addCell(String.valueOf(d.estanqueId));
            table.addCell(d.fecha.toString());
            table.addCell(String.valueOf(d.valor));
            table.addCell(d.tipo);
            table.addCell(d.unidad);
        }
        doc.add(table);
        doc.close();
        return destino;
    }
}
