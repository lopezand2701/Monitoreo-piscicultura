package com.mycompany.piscicultura_proyect.util;

// Imports explícitos para evitar colisiones y reducir warnings
import com.itextpdf.text.Document;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import com.mycompany.piscicultura_proyect.ConexionPostgres;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class ReporteUtil {

    private static class MedicionCompletaDto {
        LocalDateTime fechaHora;
        Double temperatura;
        Double ph;
        MedicionCompletaDto(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    }

    private static class AlertaDto {
        Timestamp hora;
        String tipoEvento;
        String parametro;
        Double valor;
        String descripcion;
    }

    // Consultar mediciones de temperatura y pH para un estanque y período (robustecido)
    private static List<MedicionCompletaDto> consultarMedicionesEstanque(LocalDate ini, LocalDate fin, int estanqueId) throws Exception {
        String sql = "SELECT m.fecha_hora, m.valor, s.tipo " +
                     "FROM mediciones m JOIN sensores s ON m.sensor_id = s.sensor_id " +
                     "WHERE m.estanque_id = ? " +
                     "AND m.fecha_hora::date BETWEEN ? AND ? " +
                     "AND (LOWER(s.tipo) LIKE '%temp%' OR LOWER(s.tipo) LIKE '%ph%') " +
                     "ORDER BY m.fecha_hora ASC";

        Map<LocalDateTime, MedicionCompletaDto> mapa = new TreeMap<>();
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("fecha_hora");
                    if (ts == null) continue;
                    LocalDateTime fh = ts.toLocalDateTime();
                    double valor = rs.getDouble("valor");
                    String tipo = Optional.ofNullable(rs.getString("tipo")).orElse("").toLowerCase();
                    LocalDateTime slot = redondearA15Minutos(fh);
                    MedicionCompletaDto dto = mapa.computeIfAbsent(slot, MedicionCompletaDto::new);
                    if (tipo.contains("temp")) dto.temperatura = valor;
                    else if (tipo.contains("ph")) dto.ph = valor;
                }
            }
        }
        return new ArrayList<>(mapa.values());
    }

    private static LocalDateTime redondearA15Minutos(LocalDateTime fechaHora) {
        int m = fechaHora.getMinute();
        return fechaHora.withMinute((m / 15) * 15).withSecond(0).withNano(0);
    }

    private static List<AlertaDto> consultarAlertasEstanque(LocalDate ini, LocalDate fin, int estanqueId,
                                                            Double tempMin, Double tempMax, Double phMin, Double phMax) throws Exception {
        String sql = "SELECT a.generado_en, a.tipo, a.valor " +
                     "FROM alertas a WHERE a.estanque_id = ? " +
                     "AND a.generado_en::date BETWEEN ? AND ? ORDER BY a.generado_en ASC";
        List<AlertaDto> alertas = new ArrayList<>();
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ps.setDate(2, Date.valueOf(ini));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AlertaDto a = new AlertaDto();
                    a.hora = rs.getTimestamp("generado_en");
                    a.tipoEvento = rs.getString("tipo");
                    a.valor = rs.getDouble("valor");
                    a.parametro = determinarParametroAlerta(a.tipoEvento);
                    a.descripcion = generarDescripcionAlertaDetallada(a.tipoEvento, a.valor, tempMin, tempMax, phMin, phMax);
                    alertas.add(a);
                }
            }
        }
        return alertas;
    }

    private static String determinarParametroAlerta(String tipo) {
        if (tipo == null) return "N/D";
        String tl = tipo.toLowerCase();
        if (tl.contains("ph")) return "pH";
        if (tl.contains("temp")) return "Temperatura";
        return tipo;
    }

    private static String generarDescripcionAlertaDetallada(String tipo, double valor,
                                                            Double tempMin, Double tempMax, Double phMin, Double phMax) {
        if (tipo == null) return "Alerta sin tipo";
        String tl = tipo.toLowerCase();
        if (tl.contains("ph")) {
            if (phMin != null && valor < phMin) return String.format("pH debajo del mínimo (%.1f)", phMin);
            if (phMax != null && valor > phMax) return String.format("pH encima del máximo (%.1f)", phMax);
            return String.format("pH fuera de rango (%.1f)", valor);
        }
        if (tl.contains("temp")) {
            if (tempMin != null && valor < tempMin) return String.format("Temperatura debajo del mínimo (%.1f°C)", tempMin);
            if (tempMax != null && valor > tempMax) return String.format("Temperatura encima del máximo (%.1f°C)", tempMax);
            return String.format("Temperatura fuera de rango (%.1f°C)", valor);
        }
        return String.format("Alerta %s valor %.1f", tipo, valor);
    }

    private static Map<String, Object> obtenerInfoEstanque(int estanqueId) throws Exception {
        String sql = "SELECT e.nombre, es.nombre_cientifico, es.nombre_comun, est.nombre AS estacion_nombre, " +
                     "es.temp_min, es.temp_max, es.ph_minimo, es.ph_maximo FROM estanques e " +
                     "LEFT JOIN estanque_especies ee ON e.estanque_id = ee.estanque_id " +
                     "LEFT JOIN especies es ON ee.especie_id = es.especie_id " +
                     "LEFT JOIN estaciones est ON e.estacion_id = est.estacion_id " +
                     "WHERE e.estanque_id = ? LIMIT 1";
        Map<String, Object> info = new HashMap<>();
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.put("nombre", rs.getString("nombre"));
                    info.put("nombre_cientifico", rs.getString("nombre_cientifico"));
                    info.put("nombre_comun", rs.getString("nombre_comun"));
                    info.put("estacion_nombre", rs.getString("estacion_nombre"));
                    double v;
                    v = rs.getDouble("temp_min"); info.put("temp_min", rs.wasNull()? null : v);
                    v = rs.getDouble("temp_max"); info.put("temp_max", rs.wasNull()? null : v);
                    v = rs.getDouble("ph_minimo"); info.put("ph_minimo", rs.wasNull()? null : v);
                    v = rs.getDouble("ph_maximo"); info.put("ph_maximo", rs.wasNull()? null : v);
                }
            }
        }
        return info;
    }

    private static File generarGraficaTemperatura(List<MedicionCompletaDto> mediciones,
                                                  Double tempMin, Double tempMax) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (MedicionCompletaDto m : mediciones) {
            if (m.temperatura != null) {
                String hora = m.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
                dataset.addValue(m.temperatura, "Temperatura", hora);
                if (tempMin != null) dataset.addValue(tempMin, "Temp Mín", hora);
                if (tempMax != null) dataset.addValue(tempMax, "Temp Máx", hora);
            }
        }
        String tituloDin = "Temperatura del Estanque";
        if (!mediciones.isEmpty()) {
            LocalDateTime ini = mediciones.get(0).fechaHora;
            LocalDateTime fin = mediciones.get(mediciones.size() - 1).fechaHora;
            tituloDin += " (" + ini.format(DateTimeFormatter.ofPattern("HH:mm")) + "-" + fin.format(DateTimeFormatter.ofPattern("HH:mm")) + ")";
        }
        JFreeChart chart = ChartFactory.createLineChart(tituloDin, "Hora", "°C", dataset);
        File img = File.createTempFile("chart_temp", ".png");
        ChartUtils.saveChartAsPNG(img, chart, 600, 350);
        return img;
    }

    private static File generarGraficaPH(List<MedicionCompletaDto> mediciones,
                                         Double phMin, Double phMax) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (MedicionCompletaDto m : mediciones) {
            if (m.ph != null) {
                String hora = m.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
                dataset.addValue(m.ph, "pH", hora);
                if (phMin != null) dataset.addValue(phMin, "pH Mín", hora);
                if (phMax != null) dataset.addValue(phMax, "pH Máx", hora);
            }
        }
        String tituloDin = "pH del Estanque";
        if (!mediciones.isEmpty()) {
            LocalDateTime ini = mediciones.get(0).fechaHora;
            LocalDateTime fin = mediciones.get(mediciones.size() - 1).fechaHora;
            tituloDin += " (" + ini.format(DateTimeFormatter.ofPattern("HH:mm")) + "-" + fin.format(DateTimeFormatter.ofPattern("HH:mm")) + ")";
        }
        JFreeChart chart = ChartFactory.createLineChart(tituloDin, "Hora", "pH", dataset);
        File img = File.createTempFile("chart_ph", ".png");
        ChartUtils.saveChartAsPNG(img, chart, 600, 350);
        return img;
    }

    private static boolean hayValoresFueraDeRango(List<MedicionCompletaDto> mediciones,
                                                  Double tempMin, Double tempMax, Double phMin, Double phMax) {
        for (MedicionCompletaDto m : mediciones) {
            if (m.temperatura != null) {
                if (tempMin != null && m.temperatura < tempMin) return true;
                if (tempMax != null && m.temperatura > tempMax) return true;
            }
            if (m.ph != null) {
                if (phMin != null && m.ph < phMin) return true;
                if (phMax != null && m.ph > phMax) return true;
            }
        }
        return false;
    }

    private static void agregarLineaHorizontal(Document document) {
        try {
            document.add(new Paragraph(""));
            LineSeparator ls = new LineSeparator();
            ls.setLineColor(BaseColor.BLACK);
            document.add(new Chunk(ls));
            document.add(new Paragraph(""));
        } catch (Exception ignored) {}
    }

    // Método principal para generar el reporte completo - COMPLETAMENTE MODIFICADO
    public static void generarReporteCompleto(File archivo, LocalDate fechaInicio, LocalDate fechaFin,
                                              int estanqueId, String descripcionTecnico, String nombreTecnico) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 70, 50);
            PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();
            Font fTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fSub = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font fNorm = new Font(Font.FontFamily.HELVETICA, 10);
            Font fHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

            Map<String, Object> info = obtenerInfoEstanque(estanqueId);
            List<MedicionCompletaDto> mediciones = consultarMedicionesEstanque(fechaInicio, fechaFin, estanqueId);
            Double tempMin = (Double) info.get("temp_min");
            Double tempMax = (Double) info.get("temp_max");
            Double phMin = (Double) info.get("ph_minimo");
            Double phMax = (Double) info.get("ph_maximo");
            List<AlertaDto> alertas = consultarAlertasEstanque(fechaInicio, fechaFin, estanqueId, tempMin, tempMax, phMin, phMax);
            boolean hayProblemas = hayValoresFueraDeRango(mediciones, tempMin, tempMax, phMin, phMax) || !alertas.isEmpty();

            // Título
            Paragraph titulo = new Paragraph("Informe técnico", fTitulo); titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph("Sistema de Monitoreo Piscícola", fTitulo));
            document.add(new Paragraph("Departamento de Acuicultura", fSub));
            document.add(Chunk.NEWLINE);
            document.add(titulo);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Número de Estanque: " + info.getOrDefault("nombre", String.valueOf(estanqueId)), fNorm));
            document.add(new Paragraph("Tipo de Especie: " + info.getOrDefault("nombre_comun", "No especificado"), fNorm));
            if (tempMin != null && tempMax != null)
                document.add(new Paragraph(String.format("Temperatura óptima: %.1f°C - %.1f°C", tempMin, tempMax), fNorm));
            if (phMin != null && phMax != null)
                document.add(new Paragraph(String.format("pH óptimo: %.1f - %.1f", phMin, phMax), fNorm));
            agregarLineaHorizontal(document);

            String idInf = "T-" + estanqueId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
            document.add(new Paragraph("ID Informe: " + idInf, fNorm));
            document.add(new Paragraph("GENERADO POR: " + nombreTecnico, fNorm));
            document.add(new Paragraph("FECHA Y HORA DE GENERACIÓN: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")), fNorm));
            String periodo = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("PERIODO REGISTRADO: " + periodo, fNorm));
            String estado = hayProblemas ? "INESTABLE" : "ESTABLE";
            document.add(new Paragraph("ESTADO GENERAL DEL TURNO: " + estado,
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, hayProblemas ? BaseColor.RED : BaseColor.GREEN)));
            agregarLineaHorizontal(document);

            // Tablas
            document.add(new Paragraph("Datos Recopilados del pH", fSub));
            PdfPTable tPh = crearTablaPH(mediciones, phMin, phMax);
            document.add(tPh != null ? tPh : new Paragraph("Sin datos de pH.", fNorm));
            // Gráfica pH inmediatamente después de la tabla
            if (hayDatosPH(mediciones)) {
                try {
                    File gPh = generarGraficaPH(mediciones, phMin, phMax);
                    if (gPh.exists()) { Image imgP = Image.getInstance(gPh.getAbsolutePath()); imgP.scaleToFit(500,300); document.add(imgP); }
                    if (!gPh.delete()) System.err.println("No se borró ph:" + gPh);
                } catch (Exception ex) {
                    document.add(new Paragraph("Error generando gráfica pH: " + ex.getMessage(), fNorm));
                }
            } else {
                document.add(new Paragraph("No hay datos suficientes para gráfica de pH", fNorm));
            }
            agregarLineaHorizontal(document);

            document.add(new Paragraph("Datos Recopilados de la Temperatura", fSub));
            PdfPTable tTemp = crearTablaTemperatura(mediciones, tempMin, tempMax);
            document.add(tTemp != null ? tTemp : new Paragraph("Sin datos de temperatura.", fNorm));
            // Gráfica Temperatura inmediatamente después de la tabla
            if (hayDatosTemperatura(mediciones)) {
                try {
                    File gTemp = generarGraficaTemperatura(mediciones, tempMin, tempMax);
                    if (gTemp.exists()) { Image imgT = Image.getInstance(gTemp.getAbsolutePath()); imgT.scaleToFit(500,300); document.add(imgT); }
                    if (!gTemp.delete()) System.err.println("No se borró temp:" + gTemp);
                } catch (Exception ex) {
                    document.add(new Paragraph("Error generando gráfica Temperatura: " + ex.getMessage(), fNorm));
                }
            } else {
                document.add(new Paragraph("No hay datos suficientes para gráfica de Temperatura", fNorm));
            }
            agregarLineaHorizontal(document);

            // Alertas
            if (!alertas.isEmpty()) {
                document.add(new Paragraph("Bitácora de Eventos y Alertas", fSub));
                PdfPTable tAlert = crearTablaAlertas(alertas, tempMin, tempMax, phMin, phMax);
                document.add(tAlert);
                agregarLineaHorizontal(document);
            }

            // Equipamiento
            document.add(new Paragraph("Estado de Equipamiento y Sensores", fSub));
            try { document.add(crearTablaEquipamiento(estanqueId)); } catch (Exception ex) {
                document.add(new Paragraph("Error listando equipamiento: " + ex.getMessage(), fNorm));
            }
            agregarLineaHorizontal(document);

            // Verificación
            document.add(new Paragraph("Verificación y cierre del técnico", fSub));
            document.add(new Paragraph("OBSERVACIONES Y ACCIONES PENDIENTES:", fHeader));
            document.add(new Paragraph(descripcionTecnico == null || descripcionTecnico.trim().isEmpty() ? "(Sin observaciones)" : descripcionTecnico, fNorm));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("VERIFICACIÓN DE INFORME:", fHeader));
            document.add(new Paragraph("Confirmo la revisión de los datos y registro de observaciones.", fNorm));
            document.add(new Paragraph("TÉCNICO RESPONSABLE:", fHeader));
            document.add(new Paragraph(nombreTecnico, fNorm));
            document.add(new Paragraph("_________________________", fNorm));
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.close();
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(archivo);
        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e);
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage());
        }
    }

    // Tablas auxiliares (sin throws innecesarios)
    private static PdfPTable crearTablaPH(List<MedicionCompletaDto> mediciones, Double phMin, Double phMax) {
        List<MedicionCompletaDto> lista = new ArrayList<>();
        for (MedicionCompletaDto m : mediciones) if (m.ph != null) lista.add(m);
        if (lista.isEmpty()) return null;
        PdfPTable table = new PdfPTable(4); table.setWidthPercentage(100);
        table.addCell(crearCelda("Hora", true));
        table.addCell(crearCelda("pH", true));
        table.addCell(crearCelda("Base", true));
        table.addCell(crearCelda("Rango", true));
        String base = phMin != null ? String.format("%.1f", phMin) : "N/A";
        String rango = (phMin != null && phMax != null) ? String.format("%.1f", phMax - phMin) : "N/A";
        for (MedicionCompletaDto m : lista) {
            String h = m.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
            table.addCell(crearCelda(h, false));
            table.addCell(crearCeldaColor(String.format("%.1f", m.ph), estaEnRango(m.ph, phMin, phMax)));
            table.addCell(crearCelda(base, false));
            table.addCell(crearCelda(rango, false));
        }
        return table;
    }

    private static PdfPTable crearTablaTemperatura(List<MedicionCompletaDto> mediciones, Double tempMin, Double tempMax) {
        List<MedicionCompletaDto> lista = new ArrayList<>();
        for (MedicionCompletaDto m : mediciones) if (m.temperatura != null) lista.add(m);
        if (lista.isEmpty()) return null;
        PdfPTable table = new PdfPTable(4); table.setWidthPercentage(100);
        table.addCell(crearCelda("Hora", true));
        table.addCell(crearCelda("Temp (°C)", true));
        table.addCell(crearCelda("Base", true));
        table.addCell(crearCelda("Rango", true));
        String base = tempMin != null ? String.format("%.1f", tempMin) : "N/A";
        String rango = (tempMin != null && tempMax != null) ? String.format("%.1f", tempMax - tempMin) : "N/A";
        for (MedicionCompletaDto m : lista) {
            String h = m.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
            table.addCell(crearCelda(h, false));
            table.addCell(crearCeldaColor(String.format("%.1f", m.temperatura), estaEnRango(m.temperatura, tempMin, tempMax)));
            table.addCell(crearCelda(base, false));
            table.addCell(crearCelda(rango, false));
        }
        return table;
    }

    private static PdfPTable crearTablaAlertas(List<AlertaDto> alertas, Double tempMin, Double tempMax, Double phMin, Double phMax) {
        PdfPTable t = new PdfPTable(5); t.setWidthPercentage(100);
        t.addCell(crearCelda("Hora", true));
        t.addCell(crearCelda("Evento", true));
        t.addCell(crearCelda("Parámetro", true));
        t.addCell(crearCelda("Valor", true));
        t.addCell(crearCelda("Descripción", true));
        for (AlertaDto a : alertas) {
            String h = a.hora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            t.addCell(crearCelda(h, false));
            t.addCell(crearCelda(a.tipoEvento, false));
            t.addCell(crearCelda(a.parametro, false));
            boolean enRango;
            if ("Temperatura".equalsIgnoreCase(a.parametro)) enRango = estaEnRango(a.valor, tempMin, tempMax); else if ("pH".equalsIgnoreCase(a.parametro)) enRango = estaEnRango(a.valor, phMin, phMax); else enRango = true;
            t.addCell(crearCeldaColor(String.format("%.1f", a.valor), enRango));
            t.addCell(crearCelda(a.descripcion, false));
        }
        return t;
    }

    private static PdfPTable crearTablaEquipamiento(int estanqueId) throws Exception {
        PdfPTable t = new PdfPTable(4); t.setWidthPercentage(100);
        t.addCell(crearCelda("Componente", true));
        t.addCell(crearCelda("ID", true));
        t.addCell(crearCelda("Estado", true));
        t.addCell(crearCelda("Nota", true));
        String sql = "SELECT sensor_id, tipo, modelo FROM sensores WHERE estanque_id = ?";
        try (Connection c = ConexionPostgres.getConexion(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int sid = rs.getInt("sensor_id");
                    String tipo = rs.getString("tipo");
                    String id = tipo.toUpperCase() + "-" + sid;
                    t.addCell(crearCelda("Sensor " + tipo, false));
                    t.addCell(crearCelda(id, false));
                    t.addCell(crearCelda("Funcional", false));
                    t.addCell(crearCelda("", false));
                }
            }
        }
        return t;
    }

    private static PdfPCell crearCelda(String texto, boolean header) {
        PdfPCell c = new PdfPCell(new Phrase(texto, new Font(Font.FontFamily.HELVETICA, 8, header ? Font.BOLD : Font.NORMAL)));
        c.setPadding(5); return c;
    }

    private static PdfPCell crearCeldaColor(String texto, boolean enRango) {
        BaseColor fondo = enRango ? BaseColor.GREEN : BaseColor.RED;
        Font f = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, enRango ? BaseColor.BLACK : BaseColor.WHITE);
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(fondo); c.setPadding(5); return c;
    }

    private static boolean estaEnRango(Double v, Double min, Double max) {
        if (v == null) return true; // valor nulo se muestra sin marcar
        if (min == null || max == null) return true;
        return v >= min && v <= max;
    }


    private static boolean hayDatosPH(List<MedicionCompletaDto> mediciones) {
        for (MedicionCompletaDto m : mediciones) if (m.ph != null) return true; return false;
    }
    private static boolean hayDatosTemperatura(List<MedicionCompletaDto> mediciones) {
        for (MedicionCompletaDto m : mediciones) if (m.temperatura != null) return true; return false;
    }
}
