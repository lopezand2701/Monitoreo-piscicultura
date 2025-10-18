package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.controlador.UsuarioControlador;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblEmail;
    private JLabel lblContrasena;
    private JLabel lblTitulo;
    private UsuarioControlador usuarioControlador;

    public LoginFrame() {
        usuarioControlador = new UsuarioControlador();

        // --- Configuración general ---
        setTitle("Login - Sistema de Piscicultura");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- Componentes UI ---
        lblTitulo = new JLabel("🐟 Ingreso al Sistema de Piscicultura");
        lblTitulo.setBounds(50, 20, 300, 30);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        add(lblTitulo);

        // 🔐 Información de seguridad
        JLabel lblInfoSeguridad = new JLabel("🔒 Contraseñas encriptadas con SHA-256");
        lblInfoSeguridad.setBounds(80, 45, 250, 20);
        lblInfoSeguridad.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        add(lblInfoSeguridad);

        lblEmail = new JLabel("Email:");
        lblEmail.setBounds(70, 80, 80, 25);
        add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(150, 80, 180, 25);
        add(txtEmail);

        lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(70, 120, 80, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(150, 120, 180, 25);
        add(txtContrasena);

        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(130, 180, 130, 30);
        add(btnLogin);

        // --- Acción del botón ---
        btnLogin.addActionListener(e -> iniciarSesion());

        // Enter key listener para facilitar login
        txtContrasena.addActionListener(e -> iniciarSesion());
    }

    /**
     * Verifica credenciales y redirige según el rol del usuario.
     */
    private void iniciarSesion() {
        String email = txtEmail.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (email.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar email y contraseña",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar mensaje de procesamiento
        JOptionPane.showMessageDialog(this,
                "🔐 Verificando credenciales...\nLas contraseñas se validan de forma segura con SHA-256",
                "Autenticando",
                JOptionPane.INFORMATION_MESSAGE);

        Usuario usuario = usuarioControlador.verificarLogin(email, contrasena);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this,
                    "✅ Acceso concedido\nBienvenido " + usuario.getNombre() + " (" + usuario.getRolNombre() + ")",
                    "Login exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose(); // Cierra la ventana de login

            // Redirigir según el rol
            if (usuarioControlador.esAdmin(usuario)) {
                new AdminPanel(usuario).setVisible(true);
            } else if (usuarioControlador.esPiscicultor(usuario)) {
                new PiscicultorPanel(usuario).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Rol desconocido o sin permisos.");
                new LoginFrame().setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Email o contraseña incorrectos\nVerifique sus credenciales e intente nuevamente",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE);

            // Limpiar campo de contraseña
            txtContrasena.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}