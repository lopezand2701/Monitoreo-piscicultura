package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.modelo.Usuario;

import javax.swing.*;

public class AdminPanel extends JFrame {

    private Usuario usuario;
    private JLabel lblBienvenida;
    private JButton btnGestionEstaciones;
    private JButton btnGestionEstanques;
    private JButton btnGestionUsuarios;
    private JButton btnGestionEspecies;
    private JButton btnSalir;
    private JButton btnMonitoreoTemperatura; // 🔹 Nuevo botón

    public AdminPanel(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Panel Principal - Sistema Piscicultura");
        setSize(450, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // --- Mensaje de bienvenida ---
        String rolTexto = (usuario.getRolId() == 1) ? "Administrador" : "Piscicultor";
        lblBienvenida = new JLabel("👋 Bienvenido, " + usuario.getNombre() + " (" + rolTexto + ")");
        lblBienvenida.setBounds(60, 25, 350, 30);
        add(lblBienvenida);

        // --- Botones principales ---
        btnGestionEstaciones = new JButton("Gestión de Estaciones");
        btnGestionEstaciones.setBounds(120, 80, 200, 30);
        add(btnGestionEstaciones);

        btnGestionEstanques = new JButton("Gestión de Estanques");
        btnGestionEstanques.setBounds(120, 120, 200, 30);
        add(btnGestionEstanques);

        btnGestionEspecies = new JButton("Gestión de Especies");
        btnGestionEspecies.setBounds(120, 160, 200, 30);
        add(btnGestionEspecies);

        btnGestionUsuarios = new JButton("Gestión de Usuarios");
        btnGestionUsuarios.setBounds(120, 200, 200, 30);
        add(btnGestionUsuarios);

        // 🔹 Nuevo botón para monitoreo
        btnMonitoreoTemperatura = new JButton("Monitoreo de Temperatura");
        btnMonitoreoTemperatura.setBounds(120, 240, 200, 30);
        add(btnMonitoreoTemperatura);

        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setBounds(120, 290, 200, 30);
        add(btnSalir);

        // --- Eventos ---
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // --- Restricciones según rol ---
        if (usuario.getRolId() == 1) {
            // ✅ Administrador: acceso total
            btnGestionEstaciones.addActionListener(e -> new CrudEstacionesFrame(usuario).setVisible(true));
            btnGestionEstanques.addActionListener(e -> new CrudEstanquesFrame(usuario).setVisible(true));
            btnGestionEspecies.addActionListener(e -> new CrudEspeciesFrame(usuario).setVisible(true));
            btnGestionUsuarios.addActionListener(e -> new CrudUsuariosFrame(usuario).setVisible(true));

            // 🔹 Acceso al monitoreo
            btnMonitoreoTemperatura.addActionListener(e -> new MonitoreoTemperaturaFrame().setVisible(true));

            System.out.println("✅ Panel de administrador cargado - Acceso total");

        } else if (usuario.getRolId() == 2) {
            // ⚠️ Piscicultor: solo modo lectura, sin CRUD de usuarios y especies
            btnGestionEstaciones.addActionListener(e -> new CrudEstacionesFrame(usuario).setVisible(true));
            btnGestionEstanques.addActionListener(e -> new CrudEstanquesFrame(usuario).setVisible(true));

            // Deshabilitar botones sin permisos
            btnGestionEspecies.setEnabled(false);
            btnGestionEspecies.setToolTipText("No tiene permisos para gestionar especies");

            btnGestionUsuarios.setEnabled(false);
            btnGestionUsuarios.setToolTipText("No tiene permisos para gestionar usuarios");

            // 🔹 El piscicultor también puede ver el monitoreo (solo lectura)
            btnMonitoreoTemperatura.addActionListener(e -> new MonitoreoTemperaturaFrame().setVisible(true));

            JOptionPane.showMessageDialog(this,
                    "Estás en modo solo lectura.\nNo puedes modificar estaciones, estanques, especies ni usuarios.",
                    "Modo Piscicultor",
                    JOptionPane.INFORMATION_MESSAGE);

            System.out.println("⚠️ Panel de piscicultor cargado - Modo solo lectura");
        }

        System.out.println("👤 Usuario: " + usuario.getNombre());
        System.out.println("🎭 Rol: " + usuario.getRolNombre());
        System.out.println("🔑 Rol ID: " + usuario.getRolId());
    }
}
