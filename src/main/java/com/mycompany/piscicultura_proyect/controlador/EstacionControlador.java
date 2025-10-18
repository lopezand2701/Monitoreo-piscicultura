package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.EstacionDAO;
import com.mycompany.piscicultura_proyect.modelo.Estacion;
import java.util.List;

public class EstacionControlador {

    private EstacionDAO estacionDAO;

    public EstacionControlador() {
        this.estacionDAO = new EstacionDAO();
    }

    public boolean insertar(Estacion estacion, int rol) {
        return estacionDAO.insertar(estacion, rol);
    }

    public boolean actualizar(Estacion estacion, int rol) {
        return estacionDAO.actualizar(estacion, rol);
    }

    public boolean eliminar(int id, int usuarioId) {
        return estacionDAO.eliminar(id, usuarioId);
    }

    public List<Estacion> listarPorUsuario(int usuarioId, int rol) {
        return estacionDAO.listarPorUsuario(usuarioId, rol);
    }
}
