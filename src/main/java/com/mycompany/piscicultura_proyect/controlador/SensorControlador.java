package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.SensorDAO;
import com.mycompany.piscicultura_proyect.modelo.Sensor;
import java.sql.SQLException;
import java.util.List;

public class SensorControlador {
    private final SensorDAO dao = new SensorDAO();

    public List<Sensor> listar() throws SQLException { return dao.listar(); }
    public Sensor obtener(int id) throws SQLException { return dao.obtener(id); }

    public int crear(Sensor s) throws SQLException {
        validar(s);
        return dao.crear(s);
    }

    public boolean actualizar(Sensor s) throws SQLException {
        validar(s);
        return dao.actualizar(s);
    }

    public boolean eliminar(int id) throws SQLException {
        return dao.eliminar(id);
    }

    private void validar(Sensor s) {
        if (s.getEstanqueId() <= 0) throw new IllegalArgumentException("Estanque inválido");
        if (s.getTipo() == null || s.getTipo().isBlank()) throw new IllegalArgumentException("Tipo requerido");
        if (s.getUnidad() == null || s.getUnidad().isBlank()) throw new IllegalArgumentException("Unidad requerida");
    }
}
