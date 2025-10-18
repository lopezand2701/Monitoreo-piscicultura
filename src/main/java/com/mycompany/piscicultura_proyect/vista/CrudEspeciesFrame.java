package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.controlador.EspecieControlador;
import com.mycompany.piscicultura_proyect.modelo.Especie;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CrudEspeciesFrame extends JFrame {

    private EspecieControlador especieControlador;
    private JTable tablaEspecies;
    private DefaultTableModel modeloTabla;
    private JTextField txtId, txtNombreCientifico, txtNombreComun, txtDescripcion;
    private Usuario usuarioActual;

    public CrudEspeciesFrame(Usuario usuarioActual) {
        this.especieControlador = new EspecieControlador();
        this.usuarioActual = usuarioActual;

        if (usuarioActual == null || usuarioActual.getRolId() != 1) {
            JOptionPane.showMessageDialog(this,
                    "Acceso denegado: solo los administradores pueden gestionar especies.",
                    "Permiso denegado",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        inicializarComponentes();
        cargarEspecies();
    }

    private void inicializarComponentes() {
        setTitle("Gestión de Especies - Admin");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Panel de formulario
        JPanel panelFormulario = new JPanel();
        panelFormulario.setBounds(20, 20, 400, 200);
        panelFormulario.setLayout(null);
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Especie"));

        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(20, 30, 120, 25);
        panelFormulario.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(150, 30, 200, 25);
        txtId.setEditable(false);
        panelFormulario.add(txtId);

        JLabel lblNombreCientifico = new JLabel("Nombre Científico:");
        lblNombreCientifico.setBounds(20, 70, 120, 25);
        panelFormulario.add(lblNombreCientifico);

        txtNombreCientifico = new JTextField();
        txtNombreCientifico.setBounds(150, 70, 200, 25);
        panelFormulario.add(txtNombreCientifico);

        JLabel lblNombreComun = new JLabel("Nombre Común:");
        lblNombreComun.setBounds(20, 110, 120, 25);
        panelFormulario.add(lblNombreComun);

        txtNombreComun = new JTextField();
        txtNombreComun.setBounds(150, 110, 200, 25);
        panelFormulario.add(txtNombreComun);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(20, 150, 120, 25);
        panelFormulario.add(lblDescripcion);

        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(150, 150, 200, 25);
        panelFormulario.add(txtDescripcion);

        add(panelFormulario);

        // Botones
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(50, 240, 100, 30);
        btnAgregar.addActionListener(e -> agregarEspecie());
        add(btnAgregar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 240, 100, 30);
        btnActualizar.addActionListener(e -> actualizarEspecie());
        add(btnActualizar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(270, 240, 100, 30);
        btnEliminar.addActionListener(e -> eliminarEspecie());
        add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(50, 280, 100, 30);
        btnLimpiar.addActionListener(e -> limpiarCampos());
        add(btnLimpiar);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre Científico", "Nombre Común", "Descripción"}, 0);
        tablaEspecies = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaEspecies);
        scroll.setBounds(450, 20, 420, 400);
        add(scroll);

        // Evento de selección en tabla
        tablaEspecies.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int fila = tablaEspecies.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtNombreCientifico.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtNombreComun.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtDescripcion.setText(modeloTabla.getValueAt(fila, 3).toString());
                }
            }
        });
    }

    private void cargarEspecies() {
        modeloTabla.setRowCount(0);
        List<Especie> lista = especieControlador.obtenerEspecies();
        for (Especie e : lista) {
            modeloTabla.addRow(new Object[]{
                    e.getEspecieId(),
                    e.getNombreCientifico(),
                    e.getNombreComun(),
                    e.getDescripcion()
            });
        }
    }

    private void agregarEspecie() {
        String nombreCientifico = txtNombreCientifico.getText().trim();
        String nombreComun = txtNombreComun.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombreCientifico.isEmpty() || nombreComun.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete al menos nombre científico y común");
            return;
        }

        Especie especie = new Especie(nombreCientifico, nombreComun, descripcion);

        if (especieControlador.insertarEspecie(especie)) {
            JOptionPane.showMessageDialog(this, "✅ Especie agregada correctamente");
            limpiarCampos();
            cargarEspecies();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al agregar especie");
        }
    }

    private void actualizarEspecie() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una especie para actualizar");
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        String nombreCientifico = txtNombreCientifico.getText();
        String nombreComun = txtNombreComun.getText();
        String descripcion = txtDescripcion.getText();

        Especie especie = new Especie(id, nombreCientifico, nombreComun, descripcion);

        if (especieControlador.modificarEspecie(especie)) {
            JOptionPane.showMessageDialog(this, "✅ Especie actualizada correctamente");
            limpiarCampos();
            cargarEspecies();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al actualizar especie");
        }
    }

    private void eliminarEspecie() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una especie para eliminar");
            return;
        }

        int id = Integer.parseInt(txtId.getText());

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar la especie seleccionada?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (especieControlador.eliminarEspecie(id)) {
                JOptionPane.showMessageDialog(this, "🗑 Especie eliminada correctamente");
                limpiarCampos();
                cargarEspecies();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al eliminar especie");
            }
        }
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombreCientifico.setText("");
        txtNombreComun.setText("");
        txtDescripcion.setText("");
    }
}
