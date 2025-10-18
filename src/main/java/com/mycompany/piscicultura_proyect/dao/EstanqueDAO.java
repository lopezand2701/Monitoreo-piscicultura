package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Estanque;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstanqueDAO {

    private Connection conexion;

    public EstanqueDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // ✅ Inserción (solo para admin)
    public boolean insertarEstanque(Estanque e) {
        String sql = "INSERT INTO estanques (estacion_id, nombre, volumen_m3, descripcion, creado_en) " +
                "VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, e.getEstacionId());
            ps.setString(2, e.getNombre());
            ps.setDouble(3, e.getVolumenM3());
            ps.setString(4, e.getDescripcion());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("❌ Error al insertar estanque: " + ex.getMessage());
            return false;
        }
    }

    // ✅ Actualización (solo si pertenece al usuario o si es admin)
    public boolean actualizarEstanque(Estanque e, int usuarioId, String rol) {
        if (!"admin".equals(rol)) {
            System.err.println("⚠️ Acceso denegado: solo los administradores pueden actualizar estanques.");
            return false;
        }

        // Verificar que el estacion_id sea válido (>0)
        if (e.getEstacionId() <= 0) {
            System.err.println("⚠️ El ID de estación no es válido: " + e.getEstacionId());
            return false;
        }

        String sql = "UPDATE estanques " +
                "SET estacion_id = ?, nombre = ?, volumen_m3 = ?, descripcion = ? " +
                "WHERE estanque_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, e.getEstacionId());
            ps.setString(2, e.getNombre());
            ps.setDouble(3, e.getVolumenM3());
            ps.setString(4, e.getDescripcion());
            ps.setInt(5, e.getEstanqueId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Estanque actualizado correctamente.");
                return true;
            } else {
                System.err.println("⚠️ No se encontró el estanque con ID: " + e.getEstanqueId());
                return false;
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al actualizar estanque: " + ex.getMessage());
            return false;
        }
    }

    // ✅ Eliminación (solo si pertenece o es admin)
    public boolean eliminarEstanque(int id, int usuarioId, String rol) {
        String sql;

        if ("admin".equals(rol)) {
            sql = "DELETE FROM estanques WHERE estanque_id = ?";
        } else {
            sql = "DELETE FROM estanques " +
                    "WHERE estanque_id = ? " +
                    "AND estacion_id IN ( " +
                    "    SELECT estacion_id FROM estaciones WHERE usuario_id = ? " +
                    ")";
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (!"admin".equals(rol)) ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("❌ Error al eliminar estanque: " + ex.getMessage());
            return false;
        }
    }

    // ✅ Listar estanques según rol
    public List<Estanque> listarPorUsuario(int usuarioId, String rol) {
        List<Estanque> lista = new ArrayList<>();
        String sql;

        if ("admin".equals(rol)) {
            // Admin ve todos
            sql = "SELECT e.*, es.usuario_id " +
                    "FROM estanques e " +
                    "JOIN estaciones es ON e.estacion_id = es.estacion_id " +
                    "ORDER BY e.estanque_id";
        } else {
            // Piscicultor ve solo sus estanques
            sql = "SELECT e.*, es.usuario_id " +
                    "FROM estanques e " +
                    "JOIN estaciones es ON e.estacion_id = es.estacion_id " +
                    "WHERE es.usuario_id = ? " +
                    "ORDER BY e.estanque_id";
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (!"admin".equals(rol)) ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Estanque e = new Estanque();
                e.setEstanqueId(rs.getInt("estanque_id"));
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setNombre(rs.getString("nombre"));
                e.setVolumenM3(rs.getDouble("volumen_m3"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al obtener estanques: " + ex.getMessage());
        }

        return lista;
    }
}