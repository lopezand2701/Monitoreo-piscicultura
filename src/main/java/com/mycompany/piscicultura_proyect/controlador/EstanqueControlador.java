package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.EstanqueDAO;
import com.mycompany.piscicultura_proyect.modelo.Estanque;
import java.sql.Connection;
import java.util.List;

public class EstanqueControlador {

    private EstanqueDAO dao;

    public EstanqueControlador(Connection conexion) {
        this.dao = new EstanqueDAO(conexion);
    }

    public boolean insertarEstanque(Estanque estanque) {
        return dao.insertarEstanque(estanque);
    }

    public boolean actualizarEstanque(Estanque estanque, int usuarioId, String rol) {
        return dao.actualizarEstanque(estanque, usuarioId, rol);
    }

    public boolean eliminarEstanque(int id, int usuarioId, String rol) {
        return dao.eliminarEstanque(id, usuarioId, rol);
    }

    public List<Estanque> listarPorUsuario(int usuarioId, String rol) {
        return dao.listarPorUsuario(usuarioId, rol);
    }
}

