package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.util.ReporteUtil;
import com.mycompany.piscicultura_proyect.controlador.ReporteControlador;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GenerarReporteFrame extends JFrame {

    private final int estanqueId;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final Integer sensorId; // opcional
    private final Usuario usuario;

    private JTextField txtEstanqueId;
    private JTextField txtSensorId;
    private JTextField txtFechaHora;
    private JTextArea areaDescripcion;
    private JButton btnGenerar;
    private JButton btnCancelar;

    public GenerarReporteFrame(int estanqueId, LocalDate fechaInicio, LocalDate fechaFin, Integer sensorId, Usuario usuario) {
        this.estanqueId = estanqueId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.sensorId = sensorId;
        this.usuario = usuario;
        initUI();
    }

    private void initUI() {
        setTitle("Generar Reporte - Verificación");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));

        JPanel panelTop = new JPanel(new GridLayout(4,2,8,8));
        panelTop.setBorder(new EmptyBorder(10,10,0,10));

        panelTop.add(new JLabel("Estanque ID:"));
        txtEstanqueId = new JTextField(String.valueOf(estanqueId));
        txtEstanqueId.setEditable(false);
        panelTop.add(txtEstanqueId);

        panelTop.add(new JLabel("Sensor ID (opcional):"));
        txtSensorId = new JTextField(sensorId == null ? "" : String.valueOf(sensorId));
        txtSensorId.setEditable(false);
        panelTop.add(txtSensorId);

        panelTop.add(new JLabel("Técnico:"));
        JTextField txtTecnico = new JTextField(usuario.getNombre());
        txtTecnico.setEditable(false);
        panelTop.add(txtTecnico);

        panelTop.add(new JLabel("Generado el (local):"));
        txtFechaHora = new JTextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        txtFechaHora.setEditable(false);
        panelTop.add(txtFechaHora);

        add(panelTop, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout(5,5));
        panelCenter.setBorder(new EmptyBorder(10,10,10,10));
        panelCenter.add(new JLabel("Verificación y cierre del técnico:"), BorderLayout.NORTH);
        areaDescripcion = new JTextArea(8, 40);
        areaDescripcion.setLineWrap(true);
        areaDescripcion.setWrapStyleWord(true);
        panelCenter.add(new JScrollPane(areaDescripcion), BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnGenerar = new JButton("Generar y Guardar PDF");
        btnCancelar = new JButton("Cancelar");
        panelButtons.add(btnGenerar);
        panelButtons.add(btnCancelar);
        add(panelButtons, BorderLayout.SOUTH);

        btnGenerar.addActionListener(e -> onGenerar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void onGenerar() {
        try {
            String descripcion = areaDescripcion.getText().trim();
            if (descripcion.isEmpty()) {
                int opt = JOptionPane.showConfirmDialog(this, "La sección de verificación está vacía. ¿Desea continuar sin texto?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (opt != JOptionPane.YES_OPTION) return;
            }

            // Generar archivo temporal
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File pdf = new File("reporte_estanque_" + estanqueId + "_" + timestamp + ".pdf");

            String nombreTecnico = usuario.getNombre();
            ReporteUtil.generarReporteCompleto(pdf, fechaInicio, fechaFin, estanqueId, descripcion, nombreTecnico);

            // Guardar metadatos del reporte en la BD usando controlador
            String titulo = "Reporte Estanque " + estanqueId + " - " + txtFechaHora.getText();
            ReporteControlador controlador = new ReporteControlador();
            controlador.crearReporte(estanqueId, sensorId, titulo, descripcion);

            JOptionPane.showMessageDialog(this, "✅ Reporte generado y metadatos guardados en la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generando o guardando el reporte: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}