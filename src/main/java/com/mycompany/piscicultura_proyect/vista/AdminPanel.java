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

    public AdminPanel(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Panel Principal - Sistema Piscicultura");
        setSize(450, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // --- Mensaje de bienvenida ---
        lblBienvenida = new JLabel("👋 Bienvenido, " + usuario.getNombre() + " (Administrador)");
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

        btnSalir = new JButton("Cerrar Sesión");
        btnSalir.setBounds(120, 250, 200, 30);
        add(btnSalir);

        // --- Eventos ---
        btnSalir.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // ✅ Acceso completo a las funciones CRUD
        btnGestionEstaciones.addActionListener(e -> new CrudEstacionesFrame(usuario).setVisible(true));
        btnGestionEstanques.addActionListener(e -> new CrudEstanquesFrame(usuario).setVisible(true));
        btnGestionEspecies.addActionListener(e -> new CrudEspeciesFrame(usuario).setVisible(true));
        btnGestionUsuarios.addActionListener(e -> new CrudUsuariosFrame(usuario).setVisible(true));

        System.out.println("✅ Panel de administrador cargado - Acceso total");
    }
}
