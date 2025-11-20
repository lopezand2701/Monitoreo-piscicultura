package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.modelo.Usuario;
import javax.swing.*;

public class PiscicultorPanel extends JFrame {

    private Usuario usuario;
    private JLabel lblBienvenida;
    private JButton btnMonitoreoTemperatura;
    private JButton btnSalir;

    public PiscicultorPanel(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Panel de Piscicultor - Sistema Piscicultura");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // --- Bienvenida ---
        lblBienvenida = new JLabel("👋 Bienvenido, " + usuario.getNombre() + " (Piscicultor)");
        lblBienvenida.setBounds(60, 30, 350, 25);
        add(lblBienvenida);

        // --- ÚNICO BOTÓN PERMITIDO ---
        btnMonitoreoTemperatura = new JButton("Monitoreo de Parámetros");
        btnMonitoreoTemperatura.setBounds(110, 90, 180, 35);
        add(btnMonitoreoTemperatura);

        // --- Cerrar Sesión ---
        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setBounds(110, 150, 180, 35);
        add(btnSalir);

        // --- Eventos ---
        btnMonitoreoTemperatura.addActionListener(e ->
                new MonitoreoTemperaturaFrame().setVisible(true)
        );

        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JOptionPane.showMessageDialog(this,
                "Modo Piscicultor activado.\nSolo puedes visualizar el monitoreo de parámetros.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);

        System.out.println("👨‍🌾 Panel de piscicultor cargado (solo monitoreo)");
    }
}
