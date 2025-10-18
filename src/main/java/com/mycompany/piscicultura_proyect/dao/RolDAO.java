package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Rol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    private Connection conexion;

    public RolDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // 🔹 Insertar nuevo rol
    public boolean insertar(Rol rol) {
        String sql = "INSERT INTO rol (nombre, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, rol.getNombre());
            ps.setString(2, rol.getDescripcion());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar rol: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Listar todos los roles
    public List<Rol> listar() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT * FROM rol";
        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Rol rol = new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                lista.add(rol);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar roles: " + e.getMessage());
        }
        return lista;
    }

    // 🔹 Buscar rol por ID
    public Rol obtenerPorId(int id) {
        String sql = "SELECT * FROM rol WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Rol(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener rol: " + e.getMessage());
        }
        return null;
    }

    // 🔹 Eliminar rol
    public boolean eliminar(int id) {
        String sql = "DELETE FROM rol WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar rol: " + e.getMessage());
            return false;
        }
    }
}

