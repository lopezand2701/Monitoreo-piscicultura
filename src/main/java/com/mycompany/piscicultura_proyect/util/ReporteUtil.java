package com.mycompany.piscicultura_proyect.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.*;
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
import java.util.*;

public class ReporteUtil {

    private static class MedicionCompletaDto {
        LocalDateTime fechaHora;
        Double temperatura;
        Double ph;

        public MedicionCompletaDto(LocalDateTime fechaHora) {
            this.fechaHora = fechaHora;
        }
    }

    private static class AlertaDto {
        Timestamp hora;
        String tipoEvento;
        String parametro;
        Double valor;
        String descripcion;
    }

    // Consultar mediciones de temperatura y pH para un estanque y período - CORREGIDO
    private static List<MedicionCompletaDto> consultarMedicionesEstanque(LocalDate ini, LocalDate fin, int estanqueId) throws Exception {
        List<MedicionCompletaDto> medicionesCompletas = new ArrayList<>();

        String sql = """
            SELECT m.fecha_hora, m.valor, s.tipo
            FROM mediciones m
            JOIN sensores s ON m.sensor_id = s.sensor_id
            WHERE m.estanque_id = ?
            AND DATE(m.fecha_hora) BETWEEN ? AND ?
            AND (LOWER(s.tipo) IN ('temperatura', 'ph', 'temp') OR LOWER(s.tipo) LIKE '%ph%')
            ORDER BY m.fecha_hora ASC
            """;

        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estanqueId);
            ps.setDate(2, java.sql.Date.valueOf(ini));
            ps.setDate(3, java.sql.Date.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                // Agrupar por intervalos de 15 minutos para emparejar temperatura y pH
                Map<LocalDateTime, MedicionCompletaDto> mapaMediciones = new TreeMap<>();

                while (rs.next()) {
                    Timestamp fechaHoraTimestamp = rs.getTimestamp("fecha_hora");
                    if (fechaHoraTimestamp == null) {
                        continue; // ignora filas sin fecha
                    }
                    LocalDateTime fechaHora = fechaHoraTimestamp.toLocalDateTime();
                    Object valorObj = rs.getObject("valor");
                    Double valor = null;
                    if (valorObj != null) {
                        if (valorObj instanceof Number) {
                            valor = ((Number) valorObj).doubleValue();
                        } else {
                            try {
                                valor = Double.parseDouble(valorObj.toString());
                            } catch (NumberFormatException ex) {
                                valor = null;
                            }
                        }
                    }

                    String tipo = rs.getString("tipo");
                    String tipoLower = tipo == null ? "" : tipo.toLowerCase();

                    // Redondear a intervalos de 15 minutos para agrupar
                    LocalDateTime fechaHoraRedondeada = redondearA15Minutos(fechaHora);

                    // Obtener o crear la medición para esta fecha/hora redondeada usando computeIfAbsent
                    MedicionCompletaDto medicion = mapaMediciones.computeIfAbsent(fechaHoraRedondeada,
                            MedicionCompletaDto::new);

                    // Asignar valores según el tipo de sensor
                    if (valor != null) {
                        if (tipoLower.contains("temp")) {
                            medicion.temperatura = valor;
                        } else if (tipoLower.contains("ph")) {
                            medicion.ph = valor;
                        } else if (tipoLower.contains("temperatura")) {
                            medicion.temperatura = valor;
                        }
                    }
                }

                // Convertir a lista manteniendo el orden
                medicionesCompletas.addAll(mapaMediciones.values());

                // Debug: imprimir mediciones obtenidas
                System.out.println("Mediciones obtenidas para estanque " + estanqueId + ": " + medicionesCompletas.size());
                for (MedicionCompletaDto med : medicionesCompletas) {
                    System.out.println("Hora: " + med.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm")) +
                            " - Temp: " + med.temperatura + " - pH: " + med.ph);
                }
            }
        }
        return medicionesCompletas;
    }

    // Método para redondear a intervalos de 15 minutos
    private static LocalDateTime redondearA15Minutos(LocalDateTime fechaHora) {
        int minutos = fechaHora.getMinute();
        int minutosRedondeados = (minutos / 15) * 15;
        return fechaHora.withMinute(minutosRedondeados).withSecond(0).withNano(0);
    }

    // Consultar alertas para el estanque en el período
    private static List<AlertaDto> consultarAlertasEstanque(LocalDate ini, LocalDate fin, int estanqueId) throws Exception {
        List<AlertaDto> alertas = new ArrayList<>();

        String sql = """
            SELECT a.generado_en, a.tipo, a.valor, a.rango_esperado
            FROM alertas a
            WHERE a.estanque_id = ?
            AND DATE(a.generado_en) BETWEEN ? AND ?
            ORDER BY a.generado_en ASC
            """;

        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estanqueId);
            ps.setDate(2, java.sql.Date.valueOf(ini));
            ps.setDate(3, java.sql.Date.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AlertaDto alerta = new AlertaDto();
                    alerta.hora = rs.getTimestamp("generado_en");
                    alerta.tipoEvento = rs.getString("tipo");
                    alerta.valor = rs.getDouble("valor");
                    alerta.parametro = determinarParametroAlerta(rs.getString("tipo"));
                    alerta.descripcion = generarDescripcionAlerta(rs.getString("tipo"),
                            rs.getDouble("valor"), rs.getString("rango_esperado"));

                    alertas.add(alerta);
                }
            }
        }
        return alertas;
    }

    private static String determinarParametroAlerta(String tipo) {
        if (tipo.toLowerCase().contains("ph")) return "pH";
        if (tipo.toLowerCase().contains("temp")) return "Temperatura";
        return tipo;
    }

    private static String generarDescripcionAlerta(String tipo, double valor, String rangoEsperado) {
        if (tipo.toLowerCase().contains("ph")) {
            return "pH " + (valor < 6.5 ? "por debajo" : "por encima") + " del umbral crítico (" + rangoEsperado + ")";
        } else if (tipo.toLowerCase().contains("temp")) {
            return "Temperatura " + (valor < 26 ? "por debajo" : "por encima") + " del rango óptimo (" + rangoEsperado + ")";
        }
        return "Alerta en " + tipo + " - Valor: " + valor + ", Rango esperado: " + rangoEsperado;
    }

    // Obtener información del estanque
    private static Map<String, Object> obtenerInfoEstanque(int estanqueId) throws Exception {
        Map<String, Object> info = new HashMap<>();

        String sql = """
            SELECT e.nombre, es.nombre_cientifico, es.nombre_comun, est.nombre as estacion_nombre,
                   es.temp_min, es.temp_max, es.ph_minimo, es.ph_maximo
            FROM estanques e
            LEFT JOIN estanque_especies ee ON e.estanque_id = ee.estanque_id
            LEFT JOIN especies es ON ee.especie_id = es.especie_id
            LEFT JOIN estaciones est ON e.estacion_id = est.estacion_id
            WHERE e.estanque_id = ?
            LIMIT 1
            """;

        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estanqueId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.put("nombre", rs.getString("nombre"));
                    info.put("nombre_cientifico", rs.getString("nombre_cientifico"));
                    info.put("nombre_comun", rs.getString("nombre_comun"));
                    info.put("estacion_nombre", rs.getString("estacion_nombre"));

                    // Manejar valores nulos para rangos
                    double tempMin = rs.getDouble("temp_min");
                    info.put("temp_min", rs.wasNull() ? null : tempMin);

                    double tempMax = rs.getDouble("temp_max");
                    info.put("temp_max", rs.wasNull() ? null : tempMax);

                    double phMinimo = rs.getDouble("ph_minimo");
                    info.put("ph_minimo", rs.wasNull() ? null : phMinimo);

                    double phMaximo = rs.getDouble("ph_maximo");
                    info.put("ph_maximo", rs.wasNull() ? null : phMaximo);
                }
            }
        }
        return info;
    }

    // Generar gráfica y guardar como imagen temporal - CORREGIDO
    private static File generarGraficaTemperatura(List<MedicionCompletaDto> mediciones, String titulo, Double tempMin, Double tempMax) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (MedicionCompletaDto med : mediciones) {
            if (med.temperatura != null) {
                String hora = med.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
                dataset.addValue(med.temperatura, "Temperatura", hora);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                titulo,
                "Hora del día",
                "Temperatura (°C)",
                dataset
        );

        File tempFile = File.createTempFile("chart_temp", ".png");
        ChartUtils.saveChartAsPNG(tempFile, chart, 600, 400);
        return tempFile;
    }

    private static File generarGraficaPH(List<MedicionCompletaDto> mediciones, String titulo, Double phMin, Double phMax) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (MedicionCompletaDto med : mediciones) {
            if (med.ph != null) {
                String hora = med.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
                dataset.addValue(med.ph, "pH", hora);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                titulo,
                "Hora del día",
                "pH",
                dataset
        );

        File tempFile = File.createTempFile("chart_ph", ".png");
        ChartUtils.saveChartAsPNG(tempFile, chart, 600, 400);
        return tempFile;
    }

    // Método principal para generar el reporte completo - CORREGIDO
    public static void generarReporteCompleto(File archivo, LocalDate fechaInicio, LocalDate fechaFin,
                                              int estanqueId, String descripcionTecnico, String nombreTecnico) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();

            // Configuración de fuentes
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font fontSubtitulo = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10);
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

            // Obtener información del estanque
            Map<String, Object> infoEstanque = obtenerInfoEstanque(estanqueId);
            List<MedicionCompletaDto> mediciones = consultarMedicionesEstanque(fechaInicio, fechaFin, estanqueId);
            List<AlertaDto> alertas = consultarAlertasEstanque(fechaInicio, fechaFin, estanqueId);

            // Extraer rangos
            Double tempMin = (Double) infoEstanque.get("temp_min");
            Double tempMax = (Double) infoEstanque.get("temp_max");
            Double phMin = (Double) infoEstanque.get("ph_minimo");
            Double phMax = (Double) infoEstanque.get("ph_maximo");

            // Encabezado
            Paragraph empresa = new Paragraph("Sistema de Monitoreo Piscícola", fontTitulo);
            empresa.setAlignment(Element.ALIGN_CENTER);
            document.add(empresa);

            Paragraph departamento = new Paragraph("Departamento de Acuicultura", fontSubtitulo);
            departamento.setAlignment(Element.ALIGN_CENTER);
            document.add(departamento);

            document.add(Chunk.NEWLINE);

            // Información del reporte
            Paragraph tituloReporte = new Paragraph("Informe técnico", fontTitulo);
            tituloReporte.setAlignment(Element.ALIGN_CENTER);
            document.add(tituloReporte);

            document.add(Chunk.NEWLINE);

            // Datos del estanque
            document.add(new Paragraph("Número de Estanque: " + infoEstanque.getOrDefault("nombre", String.valueOf(estanqueId)), fontNormal));
            document.add(new Paragraph("Tipo de Especie: " + infoEstanque.getOrDefault("nombre_comun", "No especificado"), fontNormal));

            // Mostrar rangos óptimos si están disponibles
            if (tempMin != null && tempMax != null) {
                document.add(new Paragraph("Temperatura óptima: " + tempMin + "°C - " + tempMax + "°C", fontNormal));
            }
            if (phMin != null && phMax != null) {
                document.add(new Paragraph("pH óptimo: " + phMin + " - " + phMax, fontNormal));
            }

            String idInforme = "T-" + estanqueId + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));
            document.add(new Paragraph("ID de Informe: " + idInforme, fontNormal));

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("GENERADO POR: " + nombreTecnico, fontNormal));
            document.add(new Paragraph("FECHA Y HORA DE GENERACIÓN: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")), fontNormal));

            String periodoRegistrado = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 06:00 a " +
                    fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 14:00";
            document.add(new Paragraph("PERIODO REGISTRADO: " + periodoRegistrado, fontNormal));

            // Determinar estado general basado en alertas
            String estadoGeneral = alertas.isEmpty() ? "ESTABLE" : "CON INCIDENCIAS CRÍTICAS";
            document.add(new Paragraph("ESTADO GENERAL DEL TURNO: " + estadoGeneral, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD,
                    alertas.isEmpty() ? BaseColor.GREEN : BaseColor.RED)));

            document.add(Chunk.NEWLINE);

            // Diagnóstico General
            Paragraph diagnosticoTitle = new Paragraph("Diagnóstico General del Periodo.", fontSubtitulo);
            document.add(diagnosticoTitle);
            document.add(new Paragraph("Evaluación del Técnico:", fontNormal));

            document.add(Chunk.NEWLINE);

            // Tabla de pH - CORREGIDA
            document.add(new Paragraph("Datos Recopilados del pH:", fontSubtitulo));
            PdfPTable tablaPh = crearTablaPH(mediciones, phMin, phMax);
            if (tablaPh != null) {
                document.add(tablaPh);
            } else {
                document.add(new Paragraph("No hay datos de pH disponibles para el período seleccionado.", fontNormal));
            }

            document.add(Chunk.NEWLINE);

            // Tabla de Temperatura - CORREGIDA
            document.add(new Paragraph("Datos Recopilados de la Temperatura:", fontSubtitulo));
            PdfPTable tablaTemp = crearTablaTemperatura(mediciones, tempMin, tempMax);
            if (tablaTemp != null) {
                document.add(tablaTemp);
            } else {
                document.add(new Paragraph("No hay datos de temperatura disponibles para el período seleccionado.", fontNormal));
            }

            document.add(Chunk.NEWLINE);

            // Gráficas
            try {
                if (!mediciones.isEmpty()) {
                    File graficaTemp = generarGraficaTemperatura(mediciones, "Temperatura del Estanque", tempMin, tempMax);
                    File graficaPh = generarGraficaPH(mediciones, "pH del Estanque", phMin, phMax);

                    if (graficaTemp.exists() && graficaTemp.length() > 0) {
                        Image imgTemp = Image.getInstance(graficaTemp.getAbsolutePath());
                        imgTemp.scaleToFit(500, 300);
                        document.add(imgTemp);
                    }

                    document.add(Chunk.NEWLINE);

                    if (graficaPh.exists() && graficaPh.length() > 0) {
                        Image imgPh = Image.getInstance(graficaPh.getAbsolutePath());
                        imgPh.scaleToFit(500, 300);
                        document.add(imgPh);
                    }

                    // Limpiar archivos temporales
                    if (!graficaTemp.delete()) {
                        System.err.println("No se pudo borrar el archivo temporal: " + graficaTemp.getAbsolutePath());
                    }
                    if (!graficaPh.delete()) {
                        System.err.println("No se pudo borrar el archivo temporal: " + graficaPh.getAbsolutePath());
                    }
                } else {
                    document.add(new Paragraph("No hay datos suficientes para generar gráficas", fontNormal));
                }

            } catch (Exception e) {
                document.add(new Paragraph("Error al generar gráficas: " + e.getMessage(), fontNormal));
                System.err.println("Error al generar gráficas: " + e);
            }

            document.add(Chunk.NEWLINE);

            // Estado del sistema
            boolean hayDatos = !mediciones.isEmpty();
            String estadoSistema;
            if (!hayDatos) {
                estadoSistema = "NO HAY DATOS DISPONIBLES para el período seleccionado.";
            } else if (alertas.isEmpty()) {
                estadoSistema = "ESTANQUE ESTABLE. Los parámetros de Temperatura y pH se mantuvieron dentro de los rangos óptimos durante todo el turno. No se requirieron acciones correctivas.";
            } else {
                estadoSistema = "ESTANQUE CON INCIDENCIAS. Se detectaron variaciones fuera de los rangos óptimos durante el turno.";
            }

            document.add(new Paragraph(estadoSistema, fontNormal));

            document.add(Chunk.NEWLINE);

            // Bitácora de eventos y alertas
            if (!alertas.isEmpty()) {
                document.add(new Paragraph("Bitácora de Eventos y Alertas", fontSubtitulo));
                PdfPTable tablaAlertas = crearTablaAlertas(alertas);
                document.add(tablaAlertas);
            }

            document.add(Chunk.NEWLINE);

            // Estado de equipamiento
            document.add(new Paragraph("Estado de Equipamiento y sensores", fontSubtitulo));
            PdfPTable tablaEquipos = crearTablaEquipamiento(estanqueId);
            document.add(tablaEquipos);

            document.add(Chunk.NEWLINE);

            // Verificación y cierre del técnico
            document.add(new Paragraph("Verificación y cierre del técnico", fontSubtitulo));
            document.add(new Paragraph("Aquí es donde el técnico valida el informe y añade su contexto humano.", fontNormal));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("OBSERVACIONES Y ACCIONES PENDIENTES:", fontHeader));
            document.add(new Paragraph(descripcionTecnico, fontNormal));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("VERIFICACIÓN DE INFORME:", fontHeader));
            document.add(new Paragraph("Confirmo que he revisado los datos de este informe y que mis observaciones han sido registradas correctamente.", fontNormal));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("TÉCNICO RESPONSABLE (Generación de Informe):", fontHeader));
            document.add(new Paragraph(nombreTecnico, fontNormal));
            document.add(new Paragraph("_________________________", fontNormal));

            document.add(Chunk.NEWLINE);
            Paragraph piePagina = new Paragraph("Generado el: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " | Sistema de Monitoreo Piscícola",
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
            piePagina.setAlignment(Element.ALIGN_CENTER);
            document.add(piePagina);

            document.close();

            // Abrir el archivo PDF automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            System.err.println("Error al generar el reporte: " + e);
            throw new RuntimeException("Error al generar el reporte: " + e.getMessage());
        }
    }

    // Métodos auxiliares para crear tablas - CORREGIDOS
    private static PdfPTable crearTablaPH(List<MedicionCompletaDto> mediciones, Double phMin, Double phMax) {
        // Filtrar mediciones que tienen datos de pH
        List<MedicionCompletaDto> medicionesConPH = new ArrayList<>();
        for (MedicionCompletaDto med : mediciones) {
            if (med.ph != null) {
                medicionesConPH.add(med);
            }
        }

        if (medicionesConPH.isEmpty()) {
            return null;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Encabezados
        table.addCell(crearCelda("Hora", true));
        table.addCell(crearCelda("pH", true));
        table.addCell(crearCelda("Zona (Base)", true));
        table.addCell(crearCelda("Zona (Rango)", true));

        // Datos - ORDENADOS POR FECHA/HORA
        for (MedicionCompletaDto med : medicionesConPH) {
            String hora = med.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
            table.addCell(crearCelda(hora, false));

            // Crear celda con color según el rango
            PdfPCell cellValor = crearCeldaConColor(String.format("%.1f", med.ph),
                    estaEnRango(med.ph, phMin, phMax));
            table.addCell(cellValor);

            table.addCell(crearCelda("6.5", false)); // Valor base fijo para pH
            table.addCell(crearCelda("1", false));   // Rango fijo para pH
        }

        return table;
    }

    private static PdfPTable crearTablaTemperatura(List<MedicionCompletaDto> mediciones, Double tempMin, Double tempMax) {
        // Filtrar mediciones que tienen datos de temperatura
        List<MedicionCompletaDto> medicionesConTemp = new ArrayList<>();
        for (MedicionCompletaDto med : mediciones) {
            if (med.temperatura != null) {
                medicionesConTemp.add(med);
            }
        }

        if (medicionesConTemp.isEmpty()) {
            return null;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Encabezados
        table.addCell(crearCelda("Hora", true));
        table.addCell(crearCelda("Temp (°C)", true));
        table.addCell(crearCelda("Zona (Base)", true));
        table.addCell(crearCelda("Zona (Rango)", true));

        // Datos - ORDENADOS POR FECHA/HORA
        for (MedicionCompletaDto med : medicionesConTemp) {
            String hora = med.fechaHora.format(DateTimeFormatter.ofPattern("HH:mm"));
            table.addCell(crearCelda(hora, false));

            // Crear celda con color según el rango
            PdfPCell cellValor = crearCeldaConColor(String.format("%.1f", med.temperatura),
                    estaEnRango(med.temperatura, tempMin, tempMax));
            table.addCell(cellValor);

            table.addCell(crearCelda("26", false));  // Valor base fijo para temperatura
            table.addCell(crearCelda("4", false));   // Rango fijo para temperatura
        }

        return table;
    }

    private static PdfPTable crearTablaAlertas(List<AlertaDto> alertas) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        table.addCell(crearCelda("Hora", true));
        table.addCell(crearCelda("Tipo de Evento", true));
        table.addCell(crearCelda("Parámetro", true));
        table.addCell(crearCelda("Valor", true));
        table.addCell(crearCelda("Descripción", true));

        for (AlertaDto alerta : alertas) {
            String hora = alerta.hora.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            table.addCell(crearCelda(hora, false));
            table.addCell(crearCelda(alerta.tipoEvento, false));
            table.addCell(crearCelda(alerta.parametro, false));
            table.addCell(crearCelda(String.format("%.1f", alerta.valor), false));
            table.addCell(crearCelda(alerta.descripcion, false));
        }

        return table;
    }

    private static PdfPTable crearTablaEquipamiento(int estanqueId) throws Exception {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        table.addCell(crearCelda("Componente", true));
        table.addCell(crearCelda("ID", true));
        table.addCell(crearCelda("Estado", true));
        table.addCell(crearCelda("Nota", true));

        // Consultar sensores del estanque
        String sql = "SELECT sensor_id, tipo, modelo FROM sensores WHERE estanque_id = ?";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estanqueId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    String id = rs.getString("tipo").toUpperCase() + "-" + rs.getInt("sensor_id");

                    table.addCell(crearCelda("Sensor " + tipo, false));
                    table.addCell(crearCelda(id, false));
                    table.addCell(crearCelda("Funcional", false));
                    table.addCell(crearCelda("", false));
                }
            }
        }

        return table;
    }

    private static PdfPCell crearCelda(String texto, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(texto,
                new Font(Font.FontFamily.HELVETICA, 8, isHeader ? Font.BOLD : Font.NORMAL)));
        cell.setPadding(5);
        return cell;
    }

    // Nuevo método para crear celdas con color
    private static PdfPCell crearCeldaConColor(String texto, boolean estaEnRango) {
        BaseColor colorFondo = estaEnRango ? BaseColor.GREEN : BaseColor.RED;
        Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL,
                estaEnRango ? BaseColor.BLACK : BaseColor.WHITE);

        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(colorFondo);
        cell.setPadding(5);
        return cell;
    }

    // Método para verificar si un valor está dentro del rango
    private static boolean estaEnRango(Double valor, Double min, Double max) {
        if (min == null || max == null) {
            return true; // Si no hay rangos definidos, se considera dentro del rango
        }
        return valor >= min && valor <= max;
    }

    // Nuevo método para guardar solo los metadatos del reporte en la BD
    public static void guardarReporteEnBD(int estanqueId, String titulo, String descripcion,
                                          Timestamp fechaInicio, Timestamp fechaFin) throws Exception {
        String sql = "INSERT INTO reportes (estanque_id, titulo, descripcion, creado_en, fecha_inicio, fecha_fin) VALUES (?, ?, ?, NOW(), ?, ?)";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ps.setString(2, titulo);
            ps.setString(3, descripcion);
            ps.setTimestamp(4, fechaInicio);
            ps.setTimestamp(5, fechaFin);
            ps.executeUpdate();
        }
    }
}

