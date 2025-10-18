package com.mycompany.piscicultura_proyect.vista;

import com.mycompany.piscicultura_proyect.controlador.EstanqueControlador;
import com.mycompany.piscicultura_proyect.controlador.EspecieControlador;
import com.mycompany.piscicultura_proyect.modelo.Estanque;
import com.mycompany.piscicultura_proyect.modelo.Especie;
import com.mycompany.piscicultura_proyect.modelo.Usuario;
import com.mycompany.piscicultura_proyect.ConexionPostgres;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CrudEstanquesFrame extends JFrame {

    private Connection conexion;
    private EstanqueControlador controlador;
    private EspecieControlador especieControlador;
    private Usuario usuario;
    private String rol;

    private JTable tablaEstanques;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtVolumen, txtDescripcion, txtEstacionId;
    private JComboBox<Especie> comboEspecies;
    private JTextField txtCantidadEspecie;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnRefrescar, btnAsignarEspecie;

    public CrudEstanquesFrame(Usuario usuario) {
        this.usuario = usuario;
        this.conexion = ConexionPostgres.getConexion();
        this.controlador = new EstanqueControlador(conexion);
        this.especieControlador = new EspecieControlador();
        this.rol = usuario.getRolId() == 1 ? "admin" : "piscicultor";

        setTitle("Gestión de Estanques");
        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        inicializarComponentes();
        cargarEstanquesUsuario();
        cargarEspecies();

        // 🔒 Si es piscicultor, bloqueamos botones de modificación
        if (usuario.getRolId() == 2) {
            btnAgregar.setEnabled(false);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnAsignarEspecie.setEnabled(false);
            txtNombre.setEditable(false);
            txtVolumen.setEditable(false);
            txtDescripcion.setEditable(false);
            txtEstacionId.setEditable(false);
            txtCantidadEspecie.setEditable(false);
            comboEspecies.setEnabled(false);
        }
    }

    private void inicializarComponentes() {
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 20, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(120, 20, 150, 25);
        add(txtNombre);

        JLabel lblVolumen = new JLabel("Volumen (m³):");
        lblVolumen.setBounds(20, 60, 100, 25);
        add(lblVolumen);

        txtVolumen = new JTextField();
        txtVolumen.setBounds(120, 60, 150, 25);
        add(txtVolumen);

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(20, 100, 100, 25);
        add(lblDescripcion);

        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(120, 100, 150, 25);
        add(txtDescripcion);

        JLabel lblEstacion = new JLabel("Estación ID:");
        lblEstacion.setBounds(20, 140, 100, 25);
        add(lblEstacion);

        txtEstacionId = new JTextField();
        txtEstacionId.setBounds(120, 140, 150, 25);
        add(txtEstacionId);

        // Sección de asignación de especies
        JLabel lblEspecie = new JLabel("Especie:");
        lblEspecie.setBounds(20, 180, 100, 25);
        add(lblEspecie);

        comboEspecies = new JComboBox<>();
        comboEspecies.setBounds(120, 180, 150, 25);
        add(comboEspecies);

        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setBounds(20, 220, 100, 25);
        add(lblCantidad);

        txtCantidadEspecie = new JTextField();
        txtCantidadEspecie.setBounds(120, 220, 150, 25);
        txtCantidadEspecie.setText("0");
        add(txtCantidadEspecie);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(300, 20, 120, 25);
        btnAgregar.addActionListener(e -> agregarEstanque());
        add(btnAgregar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(300, 60, 120, 25);
        btnActualizar.addActionListener(e -> actualizarEstanque());
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(300, 100, 120, 25);
        btnEliminar.addActionListener(e -> eliminarEstanque());
        add(btnEliminar);

        btnAsignarEspecie = new JButton("Asignar Especie");
        btnAsignarEspecie.setBounds(300, 180, 120, 25);
        btnAsignarEspecie.addActionListener(e -> asignarEspecie());
        add(btnAsignarEspecie);

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBounds(300, 220, 120, 25);
        btnRefrescar.addActionListener(e -> cargarEstanquesUsuario());
        add(btnRefrescar);

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Estación", "Nombre", "Volumen (m³)", "Descripción", "Especies", "Creado en"}, 0
        );
        tablaEstanques = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaEstanques);
        scrollPane.setBounds(20, 260, 800, 240);
        add(scrollPane);

        // ✅ Evento de selección de fila
        tablaEstanques.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int fila = tablaEstanques.getSelectedRow();
                if (fila >= 0) {
                    txtEstacionId.setText(modeloTabla.getValueAt(fila, 1).toString());
                    txtNombre.setText(modeloTabla.getValueAt(fila, 2).toString());
                    txtVolumen.setText(modeloTabla.getValueAt(fila, 3).toString());
                    txtDescripcion.setText(modeloTabla.getValueAt(fila, 4).toString());

                    // Limpiar campos de especie al seleccionar nuevo estanque
                    txtCantidadEspecie.setText("0");
                    if (comboEspecies.getItemCount() > 0) {
                        comboEspecies.setSelectedIndex(0);
                    }
                }
            }
        });
    }

    private void cargarEspecies() {
        comboEspecies.removeAllItems();
        List<Especie> especies = especieControlador.obtenerEspecies();
        for (Especie especie : especies) {
            comboEspecies.addItem(especie);
        }

        // Agregar un item vacío al inicio
        if (comboEspecies.getItemCount() > 0) {
            comboEspecies.setSelectedIndex(0);
        }
    }

    // ✅ Carga los estanques según rol y usuario
    private void cargarEstanquesUsuario() {
        modeloTabla.setRowCount(0);

        List<Estanque> lista = controlador.listarPorUsuario(usuario.getId(), rol);
        for (Estanque e : lista) {
            String especies = especieControlador.obtenerEspeciesConCantidad(e.getEstanqueId());
            modeloTabla.addRow(new Object[]{
                    e.getEstanqueId(),
                    e.getEstacionId(),
                    e.getNombre(),
                    e.getVolumenM3(),
                    e.getDescripcion(),
                    especies,
                    e.getCreadoEn()
            });
        }
    }

    // ✅ Solo admin puede agregar
    private void agregarEstanque() {
        if (usuario.getRolId() != 1) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden agregar estanques.");
            return;
        }

        Estanque e = new Estanque();
        try {
            e.setEstacionId(Integer.parseInt(txtEstacionId.getText()));
            e.setNombre(txtNombre.getText());
            e.setVolumenM3(Double.parseDouble(txtVolumen.getText()));
            e.setDescripcion(txtDescripcion.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa valores válidos.");
            return;
        }

        if (controlador.insertarEstanque(e)) {
            JOptionPane.showMessageDialog(this, "✅ Estanque agregado correctamente");
            cargarEstanquesUsuario();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Error al agregar estanque");
        }
    }

    // ✅ Solo admin puede actualizar
    private void actualizarEstanque() {
        if (usuario.getRolId() != 1) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden actualizar estanques.");
            return;
        }

        int fila = tablaEstanques.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estanque primero.");
            return;
        }

        try {
            Estanque e = new Estanque();
            e.setEstanqueId((int) modeloTabla.getValueAt(fila, 0));
            e.setEstacionId(Integer.parseInt(txtEstacionId.getText()));
            e.setNombre(txtNombre.getText());
            e.setVolumenM3(Double.parseDouble(txtVolumen.getText()));
            e.setDescripcion(txtDescripcion.getText());

            boolean actualizado = controlador.actualizarEstanque(e, usuario.getId(), "admin");

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "✅ Estanque actualizado correctamente.");
                cargarEstanquesUsuario();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "❌ No se pudo actualizar el estanque. Verifica el ID de estación.");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Verifica que el volumen sea un número válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error al actualizar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ✅ Solo admin puede eliminar
    private void eliminarEstanque() {
        if (usuario.getRolId() != 1) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden eliminar estanques.");
            return;
        }

        int fila = tablaEstanques.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estanque primero");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar estanque seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controlador.eliminarEstanque(id, usuario.getId(), rol)) {
                JOptionPane.showMessageDialog(this, "✅ Estanque eliminado correctamente");
                cargarEstanquesUsuario();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "❌ No se pudo eliminar el estanque");
            }
        }
    }

    // ✅ Asignar especie a estanque (solo admin)
    private void asignarEspecie() {
        if (usuario.getRolId() != 1) {
            JOptionPane.showMessageDialog(this, "Solo los administradores pueden asignar especies a estanques.");
            return;
        }

        int fila = tablaEstanques.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un estanque primero.");
            return;
        }

        Especie especieSeleccionada = (Especie) comboEspecies.getSelectedItem();
        if (especieSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una especie válida.");
            return;
        }

        try {
            int estanqueId = (int) modeloTabla.getValueAt(fila, 0);
            int cantidad = Integer.parseInt(txtCantidadEspecie.getText());

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a cero.");
                return;
            }

            if (especieControlador.asignarEspecieAEstanque(estanqueId, especieSeleccionada.getEspecieId(), cantidad)) {
                JOptionPane.showMessageDialog(this,
                        "✅ Especie asignada correctamente\n" +
                                "Estanque: " + modeloTabla.getValueAt(fila, 2) + "\n" +
                                "Especie: " + especieSeleccionada.getNombreComun() + "\n" +
                                "Cantidad: " + cantidad);
                cargarEstanquesUsuario();
                txtCantidadEspecie.setText("0");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error al asignar especie al estanque");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "⚠️ La cantidad debe ser un número válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error al asignar especie: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ✅ Limpiar campos del formulario
    private void limpiarCampos() {
        txtEstacionId.setText("");
        txtNombre.setText("");
        txtVolumen.setText("");
        txtDescripcion.setText("");
        txtCantidadEspecie.setText("0");
        if (comboEspecies.getItemCount() > 0) {
            comboEspecies.setSelectedIndex(0);
        }

        // Limpiar selección de tabla
        tablaEstanques.clearSelection();
    }
}