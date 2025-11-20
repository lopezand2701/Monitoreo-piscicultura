package com.mycompany.piscicultura_proyect.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

public class ReporteUtil {

    /**
     * Genera PDF con:
     * ✔ Valores actuales
     * ✔ Gráfica de Temperatura
     * ✔ Gráfica de pH (siempre positiva)
     * ❌ Sin histórico
     */
    public static File generarPDFCompleto(
            int estanqueId,
            java.util.List<Integer> sensores,
            Double tempActual,
            Double voltActual,
            Double phActual,
            File destino
    ) throws Exception {

        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, new FileOutputStream(destino));
        doc.open();

        // ---------- ENCABEZADO ----------
        doc.add(new Paragraph("REPORTE DEL ESTANQUE " + estanqueId));
        doc.add(new Paragraph("Fecha de generación: " + new java.util.Date()));
        doc.add(new Paragraph(" "));

        // ---------- TABLA DE DATOS ACTUALES ----------
        PdfPTable tablaActual = new PdfPTable(4);
        tablaActual.addCell("Temperatura Actual (°C)");
        tablaActual.addCell("Voltaje pH Actual (V)");
        tablaActual.addCell("pH Actual");
        tablaActual.addCell("Sensores incluidos");

        tablaActual.addCell(tempActual != null ? String.format("%.2f", tempActual) : "--");
        tablaActual.addCell(voltActual != null ? String.format("%.3f", voltActual) : "--");
        tablaActual.addCell(phActual != null ? String.format("%.2f", phActual) : "--");
        tablaActual.addCell(sensores.toString());

        doc.add(tablaActual);
        doc.add(new Paragraph(" "));

        // ---------- GRAFICA TEMPERATURA ----------
        if (tempActual != null) {
            Image grafTemp = generarGrafica("Temperatura", tempActual, "°C");
            doc.add(new Paragraph("Gráfica de Temperatura"));
            doc.add(grafTemp);
            doc.add(new Paragraph(" "));
        }

        // ---------- GRAFICA PH ----------
        if (phActual != null) {
            Image grafPH = generarGrafica("pH", Math.abs(phActual), "pH");
            doc.add(new Paragraph("Gráfica de pH"));
            doc.add(grafPH);
            doc.add(new Paragraph(" "));
        }

        doc.close();
        return destino;
    }


    // ======================================================
    //     GENERA GRÁFICA CON UN SOLO PUNTO ACTUAL
    // ======================================================
    private static Image generarGrafica(String titulo, double valor, String unidad) throws Exception {

        XYSeries serie = new XYSeries(titulo);
        serie.add(1, valor); // Solo un punto

        XYSeriesCollection dataset = new XYSeriesCollection(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
                titulo,
                "Tiempo",
                unidad,
                dataset
        );

        // Eje Y siempre inicia en 0
        NumberAxis axis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        axis.setAutoRangeIncludesZero(true);

        // Crear imagen temporal
        BufferedImage img = chart.createBufferedImage(900, 400);
        File tempImg = File.createTempFile("grafica_", ".png");
        ImageIO.write(img, "png", tempImg);

        return Image.getInstance(tempImg.getAbsolutePath());
    }
}
