package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.modelo.Usuario;
import javax.swing.*;

public class PiscicultorPanel extends JFrame {

    private Usuario usuario;
    private JLabel lblBienvenida;
    private JButton btnVerEstaciones;
    private JButton btnVerEstanques;
    private JButton btnVerEspecies;
    private JButton btnSalir;

    public PiscicultorPanel(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Panel de Piscicultor - Sistema Piscicultura");
        setSize(420, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // --- Bienvenida ---
        lblBienvenida = new JLabel("👋 Bienvenido, " + usuario.getNombre() + " (Piscicultor)");
        lblBienvenida.setBounds(60, 30, 300, 25);
        add(lblBienvenida);

        // --- Botones principales ---
        btnVerEstaciones = new JButton("Ver Estaciones");
        btnVerEstaciones.setBounds(110, 80, 180, 30);
        add(btnVerEstaciones);

        btnVerEstanques = new JButton("Ver Estanques");
        btnVerEstanques.setBounds(110, 120, 180, 30);
        add(btnVerEstanques);

        btnVerEspecies = new JButton("Ver Especies");
        btnVerEspecies.setBounds(110, 160, 180, 30);
        add(btnVerEspecies);

        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setBounds(110, 210, 180, 30);
        add(btnSalir);

        // --- Eventos ---
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // 🔒 Solo vista: los piscicultores no pueden modificar nada
        btnVerEstaciones.addActionListener(e -> new CrudEstacionesFrame(usuario).setVisible(true));
        btnVerEstanques.addActionListener(e -> new CrudEstanquesFrame(usuario).setVisible(true));
        btnVerEspecies.addActionListener(e -> mostrarEspeciesSoloLectura());

        JOptionPane.showMessageDialog(this,
                "Modo Piscicultor activado.\nSolo puedes visualizar estaciones, estanques y especies.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);

        System.out.println("👨‍🌾 Panel de piscicultor cargado - Modo solo lectura");
    }

    /**
     * Método para mostrar especies en modo solo lectura
     */
    private void mostrarEspeciesSoloLectura() {
        JOptionPane.showMessageDialog(this,
                "🔍 Vista de Especies (Solo Lectura)\n\n" +
                        "Puedes ver las especies disponibles pero no modificarlas.\n" +
                        "Solo los administradores pueden gestionar especies.",
                "Especies - Modo Vista",
                JOptionPane.INFORMATION_MESSAGE);

        // Aquí podrías abrir un frame de solo lectura si lo deseas
        // new VistaEspeciesFrame(usuario).setVisible(true);
    }
}