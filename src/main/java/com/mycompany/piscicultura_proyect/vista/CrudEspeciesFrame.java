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
    private JTextField txtTempMin, txtTempMax, txtPhMin, txtPhMax;
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
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // Panel de formulario
        JPanel panelFormulario = new JPanel();
        panelFormulario.setBounds(20, 20, 500, 300);
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

        // Campos para rangos de temperatura y pH
        JLabel lblTempMin = new JLabel("Temp. Mínima (°C):");
        lblTempMin.setBounds(20, 190, 120, 25);
        panelFormulario.add(lblTempMin);

        txtTempMin = new JTextField();
        txtTempMin.setBounds(150, 190, 90, 25);
        panelFormulario.add(txtTempMin);

        JLabel lblTempMax = new JLabel("Temp. Máxima (°C):");
        lblTempMax.setBounds(250, 190, 120, 25);
        panelFormulario.add(lblTempMax);

        txtTempMax = new JTextField();
        txtTempMax.setBounds(370, 190, 90, 25);
        panelFormulario.add(txtTempMax);

        JLabel lblPhMin = new JLabel("pH Mínimo:");
        lblPhMin.setBounds(20, 230, 120, 25);
        panelFormulario.add(lblPhMin);

        txtPhMin = new JTextField();
        txtPhMin.setBounds(150, 230, 90, 25);
        panelFormulario.add(txtPhMin);

        JLabel lblPhMax = new JLabel("pH Máximo:");
        lblPhMax.setBounds(250, 230, 120, 25);
        panelFormulario.add(lblPhMax);

        txtPhMax = new JTextField();
        txtPhMax.setBounds(370, 230, 90, 25);
        panelFormulario.add(txtPhMax);

        add(panelFormulario);

        // Botones
        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(50, 340, 100, 30);
        btnAgregar.addActionListener(e -> agregarEspecie());
        add(btnAgregar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 340, 100, 30);
        btnActualizar.addActionListener(e -> actualizarEspecie());
        add(btnActualizar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(270, 340, 100, 30);
        btnEliminar.addActionListener(e -> eliminarEspecie());
        add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(380, 340, 100, 30);
        btnLimpiar.addActionListener(e -> limpiarCampos());
        add(btnLimpiar);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre Científico", "Nombre Común", "Temp Min", "Temp Max", "pH Min", "pH Max", "Descripción"}, 0);
        tablaEspecies = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tablaEspecies);
        scroll.setBounds(540, 20, 530, 500);
        add(scroll);

        // Evento de selección en tabla
        tablaEspecies.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int fila = tablaEspecies.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                    txtNombreCientifico.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtNombreComun.setText(modeloTabla.getValueAt(fila, 2).toString());

                    // Manejar valores nulos en la tabla
                    Object tempMinObj = modeloTabla.getValueAt(fila, 3);
                    Object tempMaxObj = modeloTabla.getValueAt(fila, 4);
                    Object phMinObj = modeloTabla.getValueAt(fila, 5);
                    Object phMaxObj = modeloTabla.getValueAt(fila, 6);

                    txtTempMin.setText(tempMinObj != null ? tempMinObj.toString() : "");
                    txtTempMax.setText(tempMaxObj != null ? tempMaxObj.toString() : "");
                    txtPhMin.setText(phMinObj != null ? phMinObj.toString() : "");
                    txtPhMax.setText(phMaxObj != null ? phMaxObj.toString() : "");

                    txtDescripcion.setText(modeloTabla.getValueAt(fila, 7).toString());
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
                    e.getTempMin(),
                    e.getTempMax(),
                    e.getPhMinimo(),
                    e.getPhMaximo(),
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

        // Establecer rangos si están completos
        establecerRangosEspecie(especie);

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

        // Establecer rangos si están completos
        establecerRangosEspecie(especie);

        if (especieControlador.modificarEspecie(especie)) {
            JOptionPane.showMessageDialog(this, "✅ Especie actualizada correctamente");
            limpiarCampos();
            cargarEspecies();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al actualizar especie");
        }
    }

    private void establecerRangosEspecie(Especie especie) {
        // Temperatura mínima
        if (!txtTempMin.getText().trim().isEmpty()) {
            try {
                especie.setTempMin(Double.parseDouble(txtTempMin.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Temperatura mínima debe ser un número válido");
                return;
            }
        }

        // Temperatura máxima
        if (!txtTempMax.getText().trim().isEmpty()) {
            try {
                especie.setTempMax(Double.parseDouble(txtTempMax.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Temperatura máxima debe ser un número válido");
                return;
            }
        }

        // pH mínimo
        if (!txtPhMin.getText().trim().isEmpty()) {
            try {
                especie.setPhMinimo(Double.parseDouble(txtPhMin.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "pH mínimo debe ser un número válido");
                return;
            }
        }

        // pH máximo
        if (!txtPhMax.getText().trim().isEmpty()) {
            try {
                especie.setPhMaximo(Double.parseDouble(txtPhMax.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "pH máximo debe ser un número válido");
                return;
            }
        }

        // Validar que los rangos sean lógicos
        if (especie.getTempMin() != null && especie.getTempMax() != null &&
                especie.getTempMin() >= especie.getTempMax()) {
            JOptionPane.showMessageDialog(this, "La temperatura mínima debe ser menor que la máxima");
            return;
        }

        if (especie.getPhMinimo() != null && especie.getPhMaximo() != null &&
                especie.getPhMinimo() >= especie.getPhMaximo()) {
            JOptionPane.showMessageDialog(this, "El pH mínimo debe ser menor que el máximo");
            return;
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
        txtTempMin.setText("");
        txtTempMax.setText("");
        txtPhMin.setText("");
        txtPhMax.setText("");
    }
}