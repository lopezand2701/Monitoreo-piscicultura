package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.ConexionPostgres;
import com.mycompany.piscicultura_proyect.modelo.Estacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstacionDAO {

    // 🟢 Insertar — solo admin puede crear estaciones
    public boolean insertar(Estacion estacion, int rol) {
        if (rol != 1) {
            System.out.println("⛔ No autorizado: solo el administrador puede crear estaciones.");
            return false;
        }

        String sql = "INSERT INTO estaciones (usuario_id, nombre, ubicacion) VALUES (?, ?, ?)";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estacion.getUsuarioId());
            stmt.setString(2, estacion.getNombre());
            stmt.setString(3, estacion.getUbicacion());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar estación: " + e.getMessage());
            return false;
        }
    }

    // 🟢 Actualizar — solo admin puede editar (y reasignar usuario_id)
    public boolean actualizar(Estacion estacion, int rol) {
        if (rol != 1) {
            System.out.println("⛔ No autorizado: solo el administrador puede editar estaciones.");
            return false;
        }

        // 🔹 Ahora el admin puede cambiar también el usuario asignado
        String sql = "UPDATE estaciones " +
                "SET nombre = ?, ubicacion = ?, usuario_id = ? " +
                "WHERE estacion_id = ?";

        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estacion.getNombre());
            stmt.setString(2, estacion.getUbicacion());
            stmt.setInt(3, estacion.getUsuarioId());
            stmt.setInt(4, estacion.getEstacionId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar estación: " + e.getMessage());
            return false;
        }
    }

    // 🟢 Eliminar — solo admin puede eliminar
    public boolean eliminar(int estacionId, int rol) {
        if (rol != 1) {
            System.out.println("⛔ No autorizado: solo el administrador puede eliminar estaciones.");
            return false;
        }

        String sql = "DELETE FROM estaciones WHERE estacion_id=?";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estacionId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar estación: " + e.getMessage());
            return false;
        }
    }

    // 🟢 Listar — admin ve todas, piscicultor solo las suyas
    public List<Estacion> listarPorUsuario(int usuarioId, int rol) {
        List<Estacion> lista = new ArrayList<>();
        String sql;

        if (rol == 1) {
            sql = "SELECT * FROM estaciones ORDER BY estacion_id";
        } else {
            sql = "SELECT * FROM estaciones WHERE usuario_id = ? ORDER BY estacion_id";
        }

        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (rol != 1) {
                ps.setInt(1, usuarioId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Estacion e = new Estacion();
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setNombre(rs.getString("nombre"));
                e.setUbicacion(rs.getString("ubicacion"));
                e.setCreadoEn(rs.getString("creado_en"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al listar estaciones: " + ex.getMessage());
        }

        return lista;
    }
}
