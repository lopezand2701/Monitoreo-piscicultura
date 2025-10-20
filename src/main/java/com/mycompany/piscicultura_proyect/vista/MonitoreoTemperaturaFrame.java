package com.mycompany.piscicultura_proyect.vista;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.Scanner;

public class MonitoreoTemperaturaFrame extends JFrame implements Runnable {

    private JLabel lblTemperatura;
    private SerialPort puerto;
    private Thread hiloLectura;
    private volatile boolean ejecutando;

    public MonitoreoTemperaturaFrame() {
        setTitle("Monitoreo de Temperatura - DS18B20");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        lblTemperatura = new JLabel("Esperando datos...", SwingConstants.CENTER);
        lblTemperatura.setFont(new Font("Segoe UI", Font.BOLD, 32));
        add(lblTemperatura, BorderLayout.CENTER);

        // Intentar conectar al puerto
        conectarPuerto("COM5");

        // Cerrar el puerto al cerrar la ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                detenerLectura();
            }
        });
    }

    private void conectarPuerto(String portName) {
        try {
            puerto = SerialPort.getCommPort(portName);
            puerto.setBaudRate(9600);
            puerto.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

            if (!puerto.openPort()) {
                lblTemperatura.setText("❌ Puerto no disponible");
                System.err.println("No se pudo abrir el puerto " + portName);
                return;
            }

            System.out.println("✅ Puerto abierto: " + portName);

            ejecutando = true;
            hiloLectura = new Thread(this);
            hiloLectura.start();

        } catch (Exception e) {
            lblTemperatura.setText("⚠️ Error al conectar");
            System.err.println("Error al conectar: " + e.getMessage());
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
                    System.out.println("🌡 Temperatura recibida: " + temp);
                } catch (NumberFormatException e) {
                    // Ignorar líneas no numéricas
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error leyendo datos: " + e.getMessage());
        }
    }

    private void detenerLectura() {
        ejecutando = false;
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
