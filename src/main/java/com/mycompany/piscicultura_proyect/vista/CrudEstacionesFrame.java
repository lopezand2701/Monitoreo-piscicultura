package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.controlador.EstacionControlador;
import com.mycompany.piscicultura_proyect.controlador.UbicacionControlador;
import com.mycompany.piscicultura_proyect.controlador.UsuarioControlador;
import com.mycompany.piscicultura_proyect.modelo.Estacion;
import com.mycompany.piscicultura_proyect.modelo.Departamento;
import com.mycompany.piscicultura_proyect.modelo.Municipio;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

public class CrudEstacionesFrame extends JFrame {

    private EstacionControlador controlador;
    private UbicacionControlador ubicacionControlador;
    private UsuarioControlador usuarioControlador;

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtId, txtNombre, txtUbicacion;
    private JComboBox<Departamento> comboDepartamentos;
    private JComboBox<Municipio> comboMunicipios;
    private JComboBox<Usuario> comboUsuarios;

    private JButton btnAgregar, btnActualizar, btnEliminar, btnLimpiar;
    private Usuario usuario;

    public CrudEstacionesFrame(Usuario usuario) {
        this.usuario = usuario;
        this.controlador = new EstacionControlador();
        this.ubicacionControlador = new UbicacionControlador();
        this.usuarioControlador = new UsuarioControlador();

        setTitle("Gestión de Estaciones");
        setSize(950, 520);
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

        JLabel lblDepartamento = new JLabel("Departamento:");
        lblDepartamento.setBounds(30, 150, 100, 25);
        add(lblDepartamento);

        comboDepartamentos = new JComboBox<>();
        comboDepartamentos.setBounds(140, 150, 150, 25);
        comboDepartamentos.addActionListener(e -> cargarMunicipiosPorDepartamento());
        add(comboDepartamentos);

        JLabel lblMunicipio = new JLabel("Municipio:");
        lblMunicipio.setBounds(30, 190, 100, 25);
        add(lblMunicipio);

        comboMunicipios = new JComboBox<>();
        comboMunicipios.setBounds(140, 190, 150, 25);
        add(comboMunicipios);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(30, 230, 100, 25);
        add(lblUsuario);

        comboUsuarios = new JComboBox<>();
        comboUsuarios.setBounds(140, 230, 150, 25);
        add(comboUsuarios);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(30, 280, 100, 30);
        add(btnAgregar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(140, 280, 120, 30);
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(270, 280, 100, 30);
        add(btnEliminar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(380, 280, 100, 30);
        btnLimpiar.addActionListener(e -> limpiarCampos());
        add(btnLimpiar);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Ubicación", "Departamento", "Municipio", "Usuario", "Creado En"}, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(330, 30, 580, 350);
        add(scroll);

        cargarDepartamentos();
        cargarUsuarios();
        cargarDatos();

        // Si es piscicultor (rol 2), bloquea edición
        if (usuario.getRolId() == 2) {
            btnAgregar.setEnabled(false);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            txtNombre.setEditable(false);
            txtUbicacion.setEditable(false);
            comboDepartamentos.setEnabled(false);
            comboMunicipios.setEnabled(false);
            comboUsuarios.setEnabled(false);
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

                    String departamentoNombre = modelo.getValueAt(fila, 3).toString();
                    String municipioNombre = modelo.getValueAt(fila, 4).toString();
                    String usuarioNombre = modelo.getValueAt(fila, 5).toString();

                    seleccionarDepartamentoYMunicipio(departamentoNombre, municipioNombre);
                    seleccionarUsuario(usuarioNombre);
                }
            }
        });
    }

    private void cargarDepartamentos() {
        comboDepartamentos.removeAllItems();
        List<Departamento> departamentos = ubicacionControlador.obtenerTodosDepartamentos();
        for (Departamento depto : departamentos) comboDepartamentos.addItem(depto);
    }

    private void cargarMunicipiosPorDepartamento() {
        comboMunicipios.removeAllItems();
        Departamento deptoSeleccionado = (Departamento) comboDepartamentos.getSelectedItem();
        if (deptoSeleccionado != null) {
            List<Municipio> municipios = ubicacionControlador.obtenerMunicipiosPorDepartamento(deptoSeleccionado.getDepartamentoId());
            for (Municipio municipio : municipios) comboMunicipios.addItem(municipio);
        }
    }

    private void cargarUsuarios() {
        comboUsuarios.removeAllItems();
        List<Usuario> usuarios = usuarioControlador.listarTodos();
        for (Usuario u : usuarios) comboUsuarios.addItem(u);
    }

    private void seleccionarDepartamentoYMunicipio(String departamentoNombre, String municipioNombre) {
        // Buscar y seleccionar el departamento correcto
        for (int i = 0; i < comboDepartamentos.getItemCount(); i++) {
            Departamento depto = comboDepartamentos.getItemAt(i);
            if (depto.getNombre().equalsIgnoreCase(departamentoNombre)) {
                comboDepartamentos.setSelectedIndex(i);
                break;
            }
        }

        // Cargar municipios del departamento seleccionado
        cargarMunicipiosPorDepartamento();

        // Buscar y seleccionar el municipio correcto
        for (int i = 0; i < comboMunicipios.getItemCount(); i++) {
            Municipio municipio = comboMunicipios.getItemAt(i);
            if (municipio.getNombre().equalsIgnoreCase(municipioNombre)) {
                comboMunicipios.setSelectedIndex(i);
                break;
            }
        }
    }

    private void seleccionarUsuario(String nombreUsuario) {
        for (int i = 0; i < comboUsuarios.getItemCount(); i++) {
            Usuario u = comboUsuarios.getItemAt(i);
            if (u.getNombre().equalsIgnoreCase(nombreUsuario)) {
                comboUsuarios.setSelectedIndex(i);
                break;
            }
        }
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        List<Estacion> estaciones = controlador.listarPorUsuario(usuario.getId(), usuario.getRolId());
        for (Estacion e : estaciones) {
            String departamentoNombre = ubicacionControlador.obtenerNombreDepartamento(e.getDepartamentoId());
            String municipioNombre = ubicacionControlador.obtenerNombreMunicipio(e.getMunicipioId());
            String usuarioNombre = usuarioControlador.obtenerNombreUsuario(e.getUsuarioId());

            modelo.addRow(new Object[]{
                    e.getEstacionId(),
                    e.getNombre(),
                    e.getUbicacion(),
                    departamentoNombre,
                    municipioNombre,
                    usuarioNombre,
                    e.getCreadoEn()
            });
        }
    }

    private void agregarEstacion() {
        String nombre = txtNombre.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();

        if (nombre.isEmpty() || ubicacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        Departamento depto = (Departamento) comboDepartamentos.getSelectedItem();
        Municipio muni = (Municipio) comboMunicipios.getSelectedItem();
        Usuario userSel = (Usuario) comboUsuarios.getSelectedItem();

        if (depto == null || muni == null || userSel == null) {
            JOptionPane.showMessageDialog(this, "Seleccione departamento, municipio y usuario.");
            return;
        }

        Estacion e = new Estacion();
        e.setNombre(nombre);
        e.setUbicacion(ubicacion);
        e.setDepartamentoId(depto.getDepartamentoId());
        e.setMunicipioId(muni.getMunicipioId());
        e.setUsuarioId(userSel.getId());

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
        Departamento depto = (Departamento) comboDepartamentos.getSelectedItem();
        Municipio muni = (Municipio) comboMunicipios.getSelectedItem();
        Usuario userSel = (Usuario) comboUsuarios.getSelectedItem();

        if (nombre.isEmpty() || ubicacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        Estacion e = new Estacion();
        e.setEstacionId(id);
        e.setNombre(nombre);
        e.setUbicacion(ubicacion);
        e.setDepartamentoId(depto.getDepartamentoId());
        e.setMunicipioId(muni.getMunicipioId());
        e.setUsuarioId(userSel.getId());

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
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar estación seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);

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
        if (comboDepartamentos.getItemCount() > 0)
            comboDepartamentos.setSelectedIndex(0);
        comboMunicipios.removeAllItems();
        if (comboUsuarios.getItemCount() > 0)
            comboUsuarios.setSelectedIndex(0);
        tabla.clearSelection();
    }
}
