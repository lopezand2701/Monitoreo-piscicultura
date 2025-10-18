package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.ConexionPostgres;
import com.mycompany.piscicultura_proyect.dao.RolDAO;
import com.mycompany.piscicultura_proyect.modelo.Rol;
import java.sql.Connection;
import java.util.List;

public class RolControlador {

    private RolDAO rolDAO;

    public RolControlador() {
        Connection conexion = ConexionPostgres.getConexion();
        rolDAO = new RolDAO(conexion);
    }

    public boolean agregarRol(String nombre, String descripcion) {
        Rol rol = new Rol(nombre, descripcion);
        return rolDAO.insertar(rol);
    }

    public List<Rol> listarRoles() {
        return rolDAO.listar();
    }

    public Rol obtenerRolPorId(int id) {
        return rolDAO.obtenerPorId(id);
    }

    public boolean eliminarRol(int id) {
        return rolDAO.eliminar(id);
    }
}

