package com.mycompany.piscicultura_proyect.vista;

import com.fazecast.jSerialComm.SerialPort;
import com.mycompany.piscicultura_proyect.ConexionPostgres;
import com.mycompany.piscicultura_proyect.util.ReporteUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MonitoreoTemperaturaFrame extends JFrame implements Runnable {

    private JLabel lblTemperatura;
    private JComboBox<String> comboSensores;
    private JComboBox<String> comboEstanques;
    private JButton btnIniciar, btnDetener, btnReporte;

    private SerialPort puerto;
    private Thread hiloLectura;
    private volatile boolean ejecutando;

    private Map<String, Integer> mapaSensores = new HashMap<>();
    private Map<String, Integer> mapaEstanques = new HashMap<>();

    private final int INTERVALO_LECTURA = 3000;

    public MonitoreoTemperaturaFrame() {
        setTitle("Monitoreo de Temperatura - DS18B20");
        setSize(520, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // -------- Panel superior --------
        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 10, 10));
        panelSuperior.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelSuperior.add(new JLabel("Seleccionar estanque:"));
        comboEstanques = new JComboBox<>();
        panelSuperior.add(comboEstanques);
        panelSuperior.add(new JLabel("Seleccionar sensor:"));
        comboSensores = new JComboBox<>();
        panelSuperior.add(comboSensores);
        add(panelSuperior, BorderLayout.NORTH);

        // -------- Panel central --------
        lblTemperatura = new JLabel("Seleccione estanque y sensor", SwingConstants.CENTER);
        lblTemperatura.setFont(new Font("Segoe UI", Font.BOLD, 30));
        add(lblTemperatura, BorderLayout.CENTER);

        // -------- Panel inferior (botones) --------
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnIniciar = new JButton("Iniciar medición");
        btnDetener = new JButton("Detener");
        btnReporte = new JButton("📄 Generar Reporte");

        btnDetener.setEnabled(false);
        panelBotones.add(btnIniciar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnReporte);
        add(panelBotones, BorderLayout.SOUTH);

        // Cargar datos desde BD
        cargarEstanques();
        cargarSensores();

        // Eventos de botones
        btnIniciar.addActionListener(e -> iniciarLectura());
        btnDetener.addActionListener(e -> detenerLectura());
        btnReporte.addActionListener(e -> generarReporte());

        // Cerrar el puerto al cerrar ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                detenerLectura();
            }
        });
    }

    /** Genera un reporte PDF de las mediciones del estanque seleccionado */
    private void generarReporte() {
        try {
            String estanqueNombre = (String) comboEstanques.getSelectedItem();
            Integer estanqueId = mapaEstanques.get(estanqueNombre);

            if (estanqueId == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un estanque válido para generar el reporte.");
                return;
            }

            LocalDate ini = LocalDate.now().minusDays(7);
            LocalDate fin = LocalDate.now();

            File pdf = new File("reporte_estanque_" + estanqueId + ".pdf");
            com.mycompany.piscicultura_proyect.util.ReporteUtil.generarPDF(ini, fin, estanqueId, null, pdf);

            JOptionPane.showMessageDialog(this,
                    "✅ Reporte generado correctamente:\n" + pdf.getAbsolutePath(),
                    "Reporte creado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "❌ Error generando el reporte: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cargarSensores() {
        try (Connection conn = ConexionPostgres.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT sensor_id, tipo, modelo FROM sensores")) {

            comboSensores.removeAllItems();
            mapaSensores.clear();

            while (rs.next()) {
                int id = rs.getInt("sensor_id");
                String nombre = rs.getString("tipo") + " - " + rs.getString("modelo");
                comboSensores.addItem(nombre);
                mapaSensores.put(nombre, id);
            }

            if (comboSensores.getItemCount() == 0) {
                comboSensores.addItem("⚠️ No hay sensores registrados");
                comboSensores.setEnabled(false);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar sensores: " + e.getMessage());
        }
    }

    private void cargarEstanques() {
        try (Connection conn = ConexionPostgres.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT estanque_id, nombre FROM estanques")) {

            comboEstanques.removeAllItems();
            mapaEstanques.clear();

            while (rs.next()) {
                int id = rs.getInt("estanque_id");
                String nombre = rs.getString("nombre");
                comboEstanques.addItem(nombre);
                mapaEstanques.put(nombre, id);
            }

            if (comboEstanques.getItemCount() == 0) {
                comboEstanques.addItem("⚠️ No hay estanques registrados");
                comboEstanques.setEnabled(false);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estanques: " + e.getMessage());
        }
    }

    private void iniciarLectura() {
        String sensorNombre = (String) comboSensores.getSelectedItem();
        String estanqueNombre = (String) comboEstanques.getSelectedItem();

        if (sensorNombre == null || estanqueNombre == null ||
                !mapaSensores.containsKey(sensorNombre) ||
                !mapaEstanques.containsKey(estanqueNombre)) {
            JOptionPane.showMessageDialog(this, "Seleccione un sensor y un estanque válidos.");
            return;
        }

        conectarPuerto("COM5");
        btnIniciar.setEnabled(false);
        btnDetener.setEnabled(true);
    }

    private void conectarPuerto(String portName) {
        try {
            puerto = SerialPort.getCommPort(portName);
            puerto.setBaudRate(9600);
            puerto.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

            if (!puerto.openPort()) {
                JOptionPane.showMessageDialog(this, "❌ No se pudo abrir el puerto " + portName);
                return;
            }

            System.out.println("✅ Puerto abierto: " + portName);

            ejecutando = true;
            hiloLectura = new Thread(this);
            hiloLectura.start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error al conectar: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try (InputStream in = puerto.getInputStream();
             Scanner scanner = new Scanner(in)) {

            while (ejecutando && scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                try {
                    double temp = Double.parseDouble(line);

                    SwingUtilities.invokeLater(() ->
                            lblTemperatura.setText(String.format("%.2f °C", temp))
                    );

                    guardarMedicion(temp);

                    Thread.sleep(INTERVALO_LECTURA);

                } catch (NumberFormatException e) {
                    // Ignorar líneas no numéricas
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error leyendo datos: " + e.getMessage());
        }
    }

    private void guardarMedicion(double valor) {
        String sensorNombre = (String) comboSensores.getSelectedItem();
        String estanqueNombre = (String) comboEstanques.getSelectedItem();

        if (sensorNombre == null || estanqueNombre == null) return;

        Integer sensorId = mapaSensores.get(sensorNombre);
        Integer estanqueId = mapaEstanques.get(estanqueNombre);

        if (sensorId == null || estanqueId == null) return;

        String sql = """
            INSERT INTO mediciones (sensor_id, estanque_id, valor, fecha_hora)
            VALUES (?, ?, ?, NOW());
        """;

        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sensorId);
            ps.setInt(2, estanqueId);
            ps.setDouble(3, valor);
            ps.executeUpdate();

            System.out.println("✅ Guardado: Sensor " + sensorId +
                    ", Estanque " + estanqueId + ", Valor: " + valor);

        } catch (SQLException e) {
            System.err.println("⚠️ Error al guardar medición: " + e.getMessage());
        }
    }

    private void detenerLectura() {
        ejecutando = false;
        btnIniciar.setEnabled(true);
        btnDetener.setEnabled(false);

        try {
            if (puerto != null && puerto.isOpen()) {
                puerto.closePort();
                System.out.println("🔒 Puerto cerrado correctamente");
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar puerto: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MonitoreoTemperaturaFrame().setVisible(true));
    }
}
