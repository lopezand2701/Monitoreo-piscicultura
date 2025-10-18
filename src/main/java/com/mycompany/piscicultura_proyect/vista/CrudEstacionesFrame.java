package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.controlador.EstacionControlador;
import com.mycompany.piscicultura_proyect.modelo.Estacion;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

public class CrudEstacionesFrame extends JFrame {

    private EstacionControlador controlador;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtId, txtNombre, txtUbicacion, txtUsuarioId;
    private JButton btnAgregar, btnActualizar, btnEliminar;
    private Usuario usuario;

    public CrudEstacionesFrame(Usuario usuario) {
        this.usuario = usuario;
        this.controlador = new EstacionControlador();

        setTitle("Gestión de Estaciones");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(30, 30, 100, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(140, 30, 150, 25);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(30, 70, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(140, 70, 150, 25);
        add(txtNombre);

        JLabel lblUbicacion = new JLabel("Ubicación:");
        lblUbicacion.setBounds(30, 110, 100, 25);
        add(lblUbicacion);

        txtUbicacion = new JTextField();
        txtUbicacion.setBounds(140, 110, 150, 25);
        add(txtUbicacion);

        JLabel lblUsuarioId = new JLabel("Usuario ID:");
        lblUsuarioId.setBounds(30, 150, 100, 25);
        add(lblUsuarioId);

        txtUsuarioId = new JTextField();
        txtUsuarioId.setBounds(140, 150, 150, 25);
        txtUsuarioId.setEditable(usuario.getRolId() == 1); // Solo admin puede cambiar
        add(txtUsuarioId);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(30, 200, 100, 30);
        add(btnAgregar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(140, 200, 120, 30);
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(270, 200, 100, 30);
        add(btnEliminar);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Ubicación", "Usuario ID", "Creado En"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(330, 30, 440, 350);
        add(scroll);

        cargarDatos();

        // Si es piscicultor (rol 2), bloquea botones
        if (usuario.getRolId() == 2) {
            btnAgregar.setEnabled(false);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            txtUsuarioId.setEditable(false);
        }

        // --- Eventos ---
        btnAgregar.addActionListener(e -> agregarEstacion());
        btnActualizar.addActionListener(e -> actualizarEstacion());
        btnEliminar.addActionListener(e -> eliminarEstacion());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modelo.getValueAt(fila, 0).toString());
                    txtNombre.setText(modelo.getValueAt(fila, 1).toString());
                    txtUbicacion.setText(modelo.getValueAt(fila, 2).toString());
                    txtUsuarioId.setText(modelo.getValueAt(fila, 3).toString());
                }
            }
        });
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        List<Estacion> estaciones = controlador.listarPorUsuario(usuario.getId(), usuario.getRolId());
        for (Estacion e : estaciones) {
            modelo.addRow(new Object[]{
                    e.getEstacionId(),
                    e.getNombre(),
                    e.getUbicacion(),
                    e.getUsuarioId(),
                    e.getCreadoEn()
            });
        }
    }

    private void agregarEstacion() {
        String nombre = txtNombre.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        int usuarioId = usuario.getRolId() == 1 ?
                (txtUsuarioId.getText().isBlank() ? usuario.getId() : Integer.parseInt(txtUsuarioId.getText())) :
                usuario.getId();

        if (nombre.isEmpty() || ubicacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        Estacion e = new Estacion(0, usuarioId, nombre, ubicacion, null);
        boolean ok = controlador.insertar(e, usuario.getRolId());

        if (ok) {
            JOptionPane.showMessageDialog(this, "✅ Estación agregada correctamente.");
            limpiarCampos();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, "❌ No se pudo agregar la estación.");
        }
    }

    private void actualizarEstacion() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione una estación.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();
        int usuarioId = usuario.getRolId() == 1 ?
                Integer.parseInt(txtUsuarioId.getText()) : usuario.getId();

        Estacion e = new Estacion(id, usuarioId, nombre, ubicacion, null);
        boolean ok = controlador.actualizar(e, usuario.getRolId());

        if (ok) {
            JOptionPane.showMessageDialog(this, "✅ Estación actualizada correctamente.");
            limpiarCampos();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al actualizar estación.");
        }
    }

    private void eliminarEstacion() {
        if (txtId.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Seleccione una estación.");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar estación seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = controlador.eliminar(id, usuario.getRolId());
            if (ok) {
                JOptionPane.showMessageDialog(this, "🗑 Estación eliminada correctamente.");
                limpiarCampos();
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "❌ No se pudo eliminar la estación.");
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtUbicacion.setText("");
        txtUsuarioId.setText("");
    }
}
