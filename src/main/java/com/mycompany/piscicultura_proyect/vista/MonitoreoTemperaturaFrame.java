package com.mycompany.piscicultura_proyect.vista;

import com.fazecast.jSerialComm.SerialPort;
import com.mycompany.piscicultura_proyect.ConexionPostgres;
import com.mycompany.piscicultura_proyect.util.ReporteUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

public class MonitoreoTemperaturaFrame extends JFrame implements Runnable {

    private JLabel lblTemperatura;
    private JLabel lblPH;
    private JLabel lblVoltaje;

    private JComboBox<String> comboEstanques;
    private JList<String> listaSensores;
    private JCheckBox chkTodosSensores;

    private JButton btnIniciar, btnDetener, btnReporte;

    private SerialPort puerto;
    private Thread hiloLectura;
    private volatile boolean ejecutando;

    private Map<String, Integer> mapaSensores = new HashMap<>();
    private Map<String, String> mapaTiposSensores = new HashMap<>();
    private Map<String, Integer> mapaEstanques = new HashMap<>();

    private volatile Double ultimaTemperatura;
    private volatile Double ultimoPH;
    private volatile Double ultimoVoltaje;

    private static final Pattern PATRON_NUMEROS = Pattern.compile("[-+]?\\d*\\.?\\d+");


    public MonitoreoTemperaturaFrame() {

        setTitle("Monitoreo de Parámetros Fisicoquímicos");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel();
        main.setBorder(new EmptyBorder(15, 15, 15, 15));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        add(main);

        // --------------------------
        //       ESTANQUES
        // --------------------------
        JLabel lblEstanque = new JLabel("Seleccionar Estanque:");
        comboEstanques = new JComboBox<>();
        comboEstanques.setMaximumSize(new Dimension(400, 30));
        comboEstanques.addActionListener(e -> cargarSensoresPorEstanque());

        JPanel panelEstanque = new JPanel();
        panelEstanque.setLayout(new BoxLayout(panelEstanque, BoxLayout.Y_AXIS));
        panelEstanque.add(lblEstanque);
        panelEstanque.add(Box.createVerticalStrut(5));
        panelEstanque.add(comboEstanques);

        // --------------------------
        //     LISTA DE SENSORES
        // --------------------------
        JLabel lblSensores = new JLabel("Sensores disponibles:");
        chkTodosSensores = new JCheckBox("Todos los sensores");

        chkTodosSensores.addActionListener(e -> actualizarVisibilidadSensores());

        listaSensores = new JList<>();
        listaSensores.setVisibleRowCount(4);
        listaSensores.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        listaSensores.addListSelectionListener(e -> actualizarVisibilidadSensores());

        JScrollPane scrollSensores = new JScrollPane(listaSensores);
        scrollSensores.setMaximumSize(new Dimension(400, 120));

        JPanel panelSensores = new JPanel();
        panelSensores.setLayout(new BoxLayout(panelSensores, BoxLayout.Y_AXIS));
        panelSensores.add(lblSensores);
        panelSensores.add(Box.createVerticalStrut(3));
        panelSensores.add(chkTodosSensores);
        panelSensores.add(Box.createVerticalStrut(5));
        panelSensores.add(scrollSensores);

        // --------------------------
        //        VALORES
        // --------------------------
        lblTemperatura = new JLabel("Temperatura: -- °C", SwingConstants.CENTER);
        lblTemperatura.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblPH = new JLabel("pH: --", SwingConstants.CENTER);
        lblPH.setFont(new Font("Segoe UI", Font.BOLD, 22));

        lblVoltaje = new JLabel("Voltaje pH: -- V", SwingConstants.CENTER);
        lblVoltaje.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel panelValores = new JPanel(new GridLayout(3,1,5,5));
        panelValores.setMaximumSize(new Dimension(600,150));
        panelValores.add(lblTemperatura);
        panelValores.add(lblPH);
        panelValores.add(lblVoltaje);

        // --------------------------
        //        BOTONES
        // --------------------------
        btnIniciar = new JButton("Iniciar");
        btnDetener = new JButton("Detener");
        btnReporte = new JButton("📄 Generar Reporte");

        btnDetener.setEnabled(false);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnIniciar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnReporte);

        // --------------------------
        //   AGREGAR A LA VENTANA
        // --------------------------
        main.add(panelEstanque);
        main.add(Box.createVerticalStrut(15));
        main.add(panelSensores);
        main.add(Box.createVerticalStrut(25));
        main.add(panelValores);
        main.add(Box.createVerticalGlue());
        main.add(panelBotones);

        // Eventos
        btnIniciar.addActionListener(e -> iniciarLectura());
        btnDetener.addActionListener(e -> detenerLectura());
        btnReporte.addActionListener(e -> generarReporte());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                detenerLectura();
            }
        });

        cargarEstanques();
        actualizarVisibilidadSensores();
    }


    // =============================
    //  VISIBILIDAD DINÁMICA
    // =============================
    private void actualizarVisibilidadSensores() {

        List<String> seleccion = listaSensores.getSelectedValuesList();

        if (chkTodosSensores.isSelected()) {
            lblTemperatura.setVisible(true);
            lblPH.setVisible(true);
            lblVoltaje.setVisible(true);
            return;
        }

        if (seleccion.isEmpty()) {
            lblTemperatura.setVisible(false);
            lblPH.setVisible(false);
            lblVoltaje.setVisible(false);
            return;
        }

        boolean mostrarTemp = false;
        boolean mostrarPH = false;

        for (String s : seleccion) {
            String tipo = mapaTiposSensores.get(s).toLowerCase();

            if (tipo.contains("temp"))
                mostrarTemp = true;

            if (tipo.contains("ph"))
                mostrarPH = true;
        }

        lblTemperatura.setVisible(mostrarTemp);
        lblPH.setVisible(mostrarPH);
        lblVoltaje.setVisible(mostrarPH);
    }


    // =============================
    //        BD: Estanques
    // =============================
    private void cargarEstanques() {
        try (Connection conn = ConexionPostgres.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT estanque_id, nombre FROM estanques")) {

            comboEstanques.removeAllItems();
            mapaEstanques.clear();

            while (rs.next()) {
                mapaEstanques.put(rs.getString("nombre"), rs.getInt("estanque_id"));
                comboEstanques.addItem(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estanques: " + e.getMessage());
        }
    }


    // =============================
    //   BD: Sensores por estanque
    // =============================
    private void cargarSensoresPorEstanque() {
        String estanqueNombre = (String) comboEstanques.getSelectedItem();
        if (estanqueNombre == null) return;

        int estanqueId = mapaEstanques.get(estanqueNombre);

        mapaSensores.clear();
        mapaTiposSensores.clear();

        DefaultListModel<String> modelo = new DefaultListModel<>();

        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT sensor_id, tipo, modelo FROM sensores WHERE estanque_id = ?"
             )) {

            ps.setInt(1, estanqueId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("sensor_id");
                String tipo = rs.getString("tipo");
                String mod = rs.getString("modelo");

                String nombreVisible = "[" + id + "] " + tipo + " (" + mod + ")";

                modelo.addElement(nombreVisible);

                mapaSensores.put(nombreVisible, id);
                mapaTiposSensores.put(nombreVisible, tipo);
            }

            listaSensores.setModel(modelo);
            actualizarVisibilidadSensores();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // =============================
    //      INICIO LECTURA SERIAL
    // =============================
    private void iniciarLectura() {
        if (ejecutando) return;

        if (conectarPuerto("COM5")) {
            btnIniciar.setEnabled(false);
            btnDetener.setEnabled(true);
        }
    }


    private boolean conectarPuerto(String portName) {
        try {
            puerto = SerialPort.getCommPort(portName);
            puerto.setBaudRate(9600);
            puerto.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

            if (!puerto.openPort()) {
                JOptionPane.showMessageDialog(this, "No se pudo abrir " + portName);
                return false;
            }

            ejecutando = true;
            hiloLectura = new Thread(this);
            hiloLectura.start();
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + e.getMessage());
            return false;
        }
    }


    // =============================
    //       LECTURA DEL SERIAL
    // =============================
    @Override
    public void run() {
        try (InputStream in = puerto.getInputStream()) {

            StringBuilder buffer = new StringBuilder();

            while (ejecutando) {
                if (in.available() > 0) {

                    char c = (char) in.read();

                    if (c == '\n' || c == '\r') {
                        String line = buffer.toString().trim();
                        buffer.setLength(0);

                        if (!line.isEmpty())
                            procesarLineaSerial(line);

                    } else buffer.append(c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // =============================
    //  PROCESAMIENTO DE LÍNEAS
    // =============================
    private void procesarLineaSerial(String line) {

        Matcher m = PATRON_NUMEROS.matcher(line);
        List<Double> valores = new ArrayList<>();

        while (m.find()) valores.add(Double.parseDouble(m.group()));

        if (valores.size() >= 1) ultimaTemperatura = valores.get(0);
        if (valores.size() >= 2) ultimoVoltaje = valores.get(1);
        if (valores.size() >= 3) ultimoPH = valores.get(2);

        SwingUtilities.invokeLater(() -> {
            if (ultimaTemperatura != null)
                lblTemperatura.setText("Temperatura: " + String.format("%.2f °C", ultimaTemperatura));

            if (ultimoPH != null)
                lblPH.setText("pH: " + String.format("%.2f", ultimoPH));

            if (ultimoVoltaje != null)
                lblVoltaje.setText("Voltaje pH: " + String.format("%.3f V", ultimoVoltaje));

            actualizarVisibilidadSensores();
        });

        guardarMedicion();
    }


    // =============================
    //      GUARDAR MEDICIÓN BD
    // =============================
    private void guardarMedicion() {

        Integer estanqueId = mapaEstanques.get(comboEstanques.getSelectedItem());
        if (estanqueId == null) return;

        List<String> seleccion = listaSensores.getSelectedValuesList();

        if (chkTodosSensores.isSelected())
            seleccion = new ArrayList<>(mapaSensores.keySet());

        if (seleccion.isEmpty()) return;

        for (String visible : seleccion) {

            Integer sensorId = mapaSensores.get(visible);
            String tipo = mapaTiposSensores.get(visible).toLowerCase();

            Double valor;

            if (tipo.contains("ph")) valor = ultimoPH;
            else valor = ultimaTemperatura;

            if (valor == null) continue;

            try (Connection conn = ConexionPostgres.getConexion();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO mediciones (sensor_id, estanque_id, valor, fecha_hora) VALUES (?, ?, ?, NOW())"
                 )) {

                ps.setInt(1, sensorId);
                ps.setInt(2, estanqueId);
                ps.setDouble(3, valor);
                ps.executeUpdate();

            } catch (SQLException e) {
                System.err.println("ERROR BD: " + e.getMessage());
            }
        }
    }


    // =============================
    //     GENERAR REPORTE PDF
    // =============================
    private void generarReporte() {

        try {

            Integer estanqueId = mapaEstanques.get(comboEstanques.getSelectedItem());
            if (estanqueId == null) return;

            List<Integer> sensoresSeleccionados = new ArrayList<>();

            if (chkTodosSensores.isSelected())
                sensoresSeleccionados.addAll(mapaSensores.values());
            else
                for (String s : listaSensores.getSelectedValuesList())
                    sensoresSeleccionados.add(mapaSensores.get(s));

            if (sensoresSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione al menos un sensor.");
                return;
            }

            File pdf = new File("reporte_estanque_" + estanqueId + ".pdf");

            ReporteUtil.generarPDFCompleto(
                    estanqueId,
                    sensoresSeleccionados,
                    ultimaTemperatura,
                    ultimoVoltaje,
                    ultimoPH,
                    pdf
            );

            JOptionPane.showMessageDialog(this,
                    "Reporte generado en:\n" + pdf.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void detenerLectura() {
        ejecutando = false;

        if (puerto != null && puerto.isOpen())
            puerto.closePort();

        btnIniciar.setEnabled(true);
        btnDetener.setEnabled(false);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MonitoreoTemperaturaFrame().setVisible(true)
        );
    }
}
