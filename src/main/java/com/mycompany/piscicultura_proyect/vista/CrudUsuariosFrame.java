package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.controlador.UsuarioControlador;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CrudUsuariosFrame extends JFrame {

    private UsuarioControlador usuarioControlador;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;
    private JTextField txtId, txtNombre, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private Usuario usuarioActual; // usuario logueado

    public CrudUsuariosFrame(Usuario usuarioActual) {
        this.usuarioControlador = new UsuarioControlador();
        this.usuarioActual = usuarioActual;

        if (usuarioActual == null || usuarioActual.getRolId() != 1) {
            JOptionPane.showMessageDialog(this,
                    "Acceso denegado: solo los administradores pueden gestionar usuarios.",
                    "Permiso denegado",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        inicializarComponentes();
        cargarUsuarios();
    }

    private void inicializarComponentes() {
        setTitle("Gestión de Usuarios - Admin");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(30, 30, 80, 25);
        add(lblId);
        txtId = new JTextField();
        txtId.setBounds(120, 30, 150, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 70, 80, 25);
        add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(120, 70, 150, 25);
        add(txtNombre);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setBounds(30, 110, 80, 25);
        add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(120, 110, 150, 25);
        add(txtEmail);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(30, 150, 80, 25);
        add(lblPassword);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 150, 150, 25);
        add(txtPassword);

        // 🔐 Información sobre contraseñas
        JLabel lblInfoPassword = new JLabel("(Mínimo 6 caracteres - Se encriptará automáticamente)");
        lblInfoPassword.setBounds(280, 150, 300, 25);
        lblInfoPassword.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 10));
        add(lblInfoPassword);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(30, 190, 80, 25);
        add(lblRol);

        comboRol = new JComboBox<>(new String[]{"Administrador", "Piscicultor"});
        comboRol.setBounds(120, 190, 150, 25);
        add(comboRol);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(30, 240, 100, 30);
        add(btnAgregar);
        btnAgregar.addActionListener(e -> agregarUsuario());

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(140, 240, 120, 30);
        add(btnActualizar);
        btnActualizar.addActionListener(e -> actualizarUsuario());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(270, 240, 100, 30);
        add(btnEliminar);
        btnEliminar.addActionListener(e -> eliminarUsuario());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(380, 240, 100, 30);
        add(btnLimpiar);
        btnLimpiar.addActionListener(e -> limpiarCampos());

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Email", "Rol"}, 0);
        tablaUsuarios = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBounds(320, 30, 440, 350);
        add(scroll);

        tablaUsuarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int fila = tablaUsuarios.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtEmail.setText(modeloTabla.getValueAt(fila, 2).toString());
                    comboRol.setSelectedItem(modeloTabla.getValueAt(fila, 3).toString());
                    // 🔐 No mostramos la contraseña por seguridad
                    txtPassword.setText("");
                }
            }
        });
    }

    private void cargarUsuarios() {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = usuarioControlador.obtenerUsuarios();
        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                    u.getId(),
                    u.getNombre(),
                    u.getEmail(),
                    u.getRolNombre() != null ? u.getRolNombre() : (u.getRolId() == 1 ? "Administrador" : "Piscicultor")
            });
        }
    }

    private void agregarUsuario() {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 6 caracteres",
                    "Contraseña débil",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int rolId = comboRol.getSelectedItem().equals("Administrador") ? 1 : 2;
        Usuario usuario = new Usuario(nombre, email, password, rolId);

        if (usuarioControlador.insertarUsuario(usuario)) {
            JOptionPane.showMessageDialog(this, "✅ Usuario agregado correctamente\n🔐 Contraseña encriptada con SHA-256");
            limpiarCampos();
            cargarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al agregar usuario");
        }
    }

    private void actualizarUsuario() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());
        int rolId = comboRol.getSelectedItem().equals("Administrador") ? 1 : 2;

        // 🔐 Si no se cambia la contraseña, mantener la actual
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese una nueva contraseña para actualizar",
                    "Contraseña requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 6 caracteres",
                    "Contraseña débil",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = new Usuario(id, nombre, email, password, rolId);

        if (usuarioControlador.modificarUsuario(usuario)) {
            JOptionPane.showMessageDialog(this, "✅ Usuario actualizado correctamente\n🔐 Contraseña encriptada con SHA-256");
            limpiarCampos();
            cargarUsuarios();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al actualizar usuario");
        }
    }

    private void eliminarUsuario() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar");
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el usuario seleccionado?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (usuarioControlador.eliminarUsuario(id)) {
                JOptionPane.showMessageDialog(this, "🗑 Usuario eliminado correctamente");
                limpiarCampos();
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al eliminar usuario");
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
    }
}